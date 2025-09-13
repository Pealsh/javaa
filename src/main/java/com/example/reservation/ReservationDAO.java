package com.example.reservation; 
 
import java.io.*; 
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.time.format.DateTimeParseException; 
import java.util.ArrayList; 
import java.util.Comparator; 
import java.util.List; 
import java.util.concurrent.CopyOnWriteArrayList; 
import java.util.concurrent.atomic.AtomicInteger; 
import java.util.stream.Collectors; 
 
public class ReservationDAO { 
    private static final List<Reservation> reservations = new CopyOnWriteArrayList<>(); 
    private static final AtomicInteger idCounter = new AtomicInteger(0); 
    private static final String DATA_FILE = "reservations.dat"; 
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME; 
 
    static { 
        loadReservations(); 
    } 
 
    public List<Reservation> getAllReservations() { 
        return new ArrayList<>(reservations); 
    } 
 
    public Reservation getReservationById(int id) { 
        return reservations.stream() 
                .filter(r -> r.getId() == id) 
                .findFirst() 
                .orElse(null); 
    } 
 
    // 基本的な予約追加（既存コードとの互換性のため）
    public boolean addReservation(String name, LocalDateTime reservationTime) { 
        if (isDuplicate(name, reservationTime)) { 
            return false; 
        } 
        int id = idCounter.incrementAndGet(); 
        reservations.add(new Reservation(id, name, reservationTime)); 
        saveReservations(); 
        return true; 
    }
    
    // 医療クリニック用の拡張予約追加
    public boolean addMedicalReservation(String name, LocalDateTime reservationTime, String phoneNumber,
                                       String department, String doctor, String symptoms, String insuranceNumber,
                                       String emergencyContact, String allergies, String medications, String notes) {
        if (isDuplicate(name, reservationTime)) { 
            return false; 
        } 
        int id = idCounter.incrementAndGet(); 
        Reservation reservation = new Reservation(id, name, reservationTime, phoneNumber, department, doctor,
                                                symptoms, insuranceNumber, emergencyContact, allergies, medications, notes);
        reservations.add(reservation); 
        saveReservations(); 
        return true;
    }
    // 基本的な予約更新（既存コードとの互換性のため）
    public boolean updateReservation(int id, String name, LocalDateTime reservationTime) { 
        if (isDuplicate(name, reservationTime, id)) { 
            return false; 
        } 
        for (int i = 0; i < reservations.size(); i++) { 
            if (reservations.get(i).getId() == id) { 
                // 既存の医療情報を保持
                Reservation existing = reservations.get(i);
                Reservation updated = new Reservation(id, name, reservationTime, 
                    existing.getPhoneNumber(), existing.getDepartment(), existing.getDoctor(),
                    existing.getSymptoms(), existing.getInsuranceNumber(), existing.getEmergencyContact(),
                    existing.getAllergies(), existing.getMedications(), existing.getNotes());
                reservations.set(i, updated); 
                saveReservations(); 
                return true; 
            } 
        } 
        return false; 
    }
    
    // 医療クリニック用の拡張予約更新
    public boolean updateMedicalReservation(int id, String name, LocalDateTime reservationTime, String phoneNumber,
                                          String department, String doctor, String symptoms, String insuranceNumber,
                                          String emergencyContact, String allergies, String medications, String notes) {
        if (isDuplicate(name, reservationTime, id)) { 
            return false; 
        } 
        for (int i = 0; i < reservations.size(); i++) { 
            if (reservations.get(i).getId() == id) { 
                Reservation updated = new Reservation(id, name, reservationTime, phoneNumber, department, doctor,
                                                    symptoms, insuranceNumber, emergencyContact, allergies, medications, notes);
                reservations.set(i, updated); 
                saveReservations(); 
                return true; 
            } 
        } 
        return false;
    } 
 
    public boolean deleteReservation(int id) { 
        boolean removed = reservations.removeIf(r -> r.getId() == id); 
        if (removed) { 
            saveReservations(); 
        } 
        return removed; 
    } 
 
    public void cleanUpPastReservations() { 
        int initialSize = reservations.size();
        
        // まず全ての予約をキャンセル状態にする
        for (Reservation reservation : reservations) {
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        }
        
        // その後、全ての予約を削除
        reservations.clear();
        
        int deletedCount = initialSize;
        System.out.println("クリーンアップ完了: " + deletedCount + "件の予約をキャンセル後削除しました");
        
        saveReservations(); 
    } 
 
    public List<Reservation> searchAndSortReservations(String searchTerm, String sortBy, String sortOrder) { 
        List<Reservation> filteredList = reservations.stream()
                .filter(r -> searchTerm == null || searchTerm.trim().isEmpty() ||
                        r.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        r.getReservationTime().format(FORMATTER).contains(searchTerm))
                .collect(Collectors.toList()); 
 
        Comparator<Reservation> comparator = null; 
        if ("name".equals(sortBy)) { 
            comparator = Comparator.comparing(Reservation::getName); 
        } else if ("time".equals(sortBy)) { 
            comparator = Comparator.comparing(Reservation::getReservationTime); 
        } 
 
        if (comparator != null) { 
            if ("desc".equals(sortOrder)) { 
                filteredList.sort(comparator.reversed()); 
            } else { 
                filteredList.sort(comparator); 
            } 
        } 
        return filteredList; 
    } 
 
    public void importReservations(BufferedReader reader) throws IOException { 
        String line; 
        boolean isFirstLine = true;
        int importedCount = 0;
        int skippedCount = 0;
        
        while ((line = reader.readLine()) != null) { 
            line = line.trim();
            if (line.isEmpty()) continue;
            
            if (isFirstLine && (line.contains("ID") || line.contains("名前") || line.contains("予約日時"))) {
                isFirstLine = false;
                continue;
            }
            isFirstLine = false;
            
            String[] parts = line.split(",");
            if (parts.length >= 2) { 
                try {
                    String name;
                    LocalDateTime time;
                    
                    if (parts.length == 2) {
                        // ID無しの場合：名前,日時
                        name = parts[0].trim();
                        time = parseDateTime(parts[1].trim());
                    } else {
                        // ID有りの場合：ID,名前,日時
                        name = parts[1].trim();
                        time = parseDateTime(parts[2].trim());
                    }
                    
                    if (!isDuplicate(name, time)) { 
                        int newId = idCounter.incrementAndGet();
                        reservations.add(new Reservation(newId, name, time)); 
                        importedCount++;
                    } else {
                        skippedCount++;
                        System.out.println("重複のためスキップ: " + name + " - " + time);
                    }
                } catch (Exception e) { 
                    skippedCount++;
                    System.err.println("無効なCSV行をスキップ: " + line + " - " + e.getMessage()); 
                } 
            } else {
                skippedCount++;
                System.err.println("不正なフォーマットの行をスキップ: " + line);
            }
        } 
        
        System.out.println("CSVインポート完了: " + importedCount + "件追加, " + skippedCount + "件スキップ");
        saveReservations(); 
    }
    
    private LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        String[] formats = {
            "yyyy-MM-dd'T'HH:mm",           // 2023-12-25T14:30
            "yyyy-MM-dd HH:mm",             // 2023-12-25 14:30  
            "yyyy/MM/dd HH:mm",             // 2023/12/25 14:30
            "yyyy-MM-dd'T'HH:mm:ss"         // 2023-12-25T14:30:00
        };
        
        for (String format : formats) {
            try {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException e) {

            }
        }
        

        throw new DateTimeParseException("サポートされていない日時フォーマット: " + dateTimeStr, dateTimeStr, 0);
    } 
 
    private boolean isDuplicate(String name, LocalDateTime time) { 
        return reservations.stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(name) &&
                        r.getReservationTime().equals(time)); 
    } 
 
    private boolean isDuplicate(String name, LocalDateTime time, int excludeId) { 
        return reservations.stream()
                .anyMatch(r -> r.getId() != excludeId && r.getName().equalsIgnoreCase(name) &&
                        r.getReservationTime().equals(time)); 
    } 
 
    private static void saveReservations() { 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) { 
            for (Reservation res : reservations) { 
                writer.write(String.format("%d|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s%n", 
                        res.getId(), 
                        res.getName() != null ? res.getName() : "",
                        res.getReservationTime().format(FORMATTER),
                        res.getPhoneNumber() != null ? res.getPhoneNumber() : "",
                        res.getDepartment() != null ? res.getDepartment() : "",
                        res.getDoctor() != null ? res.getDoctor() : "",
                        res.getSymptoms() != null ? res.getSymptoms().replace("|", "｜") : "",
                        res.getInsuranceNumber() != null ? res.getInsuranceNumber() : "",
                        res.getEmergencyContact() != null ? res.getEmergencyContact() : "",
                        res.getAllergies() != null ? res.getAllergies().replace("|", "｜") : "",
                        res.getMedications() != null ? res.getMedications().replace("|", "｜") : "",
                        res.getNotes() != null ? res.getNotes().replace("|", "｜") : "",
                        res.getStatus() != null ? res.getStatus().name() : "PENDING")); 
            } 
        } catch (IOException e) { 
            System.err.println("Error saving reservations: " + e.getMessage()); 
        } 
    } 
 
    private static void loadReservations() { 
        File file = new File(DATA_FILE); 
        if (!file.exists()) { 
            return; 
        } 
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) { 
            String line; 
            int maxId = 0; 
            while ((line = reader.readLine()) != null) { 
                String[] parts = line.split("\\|", -1); 
                if (parts.length >= 12) { 
                    try { 
                        int id = Integer.parseInt(parts[0]); 
                        String name = parts[1]; 
                        LocalDateTime time = LocalDateTime.parse(parts[2], FORMATTER); 
                        String phoneNumber = parts[3];
                        String department = parts[4];
                        String doctor = parts[5];
                        String symptoms = parts[6].replace("｜", "|");
                        String insuranceNumber = parts[7];
                        String emergencyContact = parts[8];
                        String allergies = parts[9].replace("｜", "|");
                        String medications = parts[10].replace("｜", "|");
                        String notes = parts[11].replace("｜", "|");
                        Reservation.ReservationStatus status = Reservation.ReservationStatus.PENDING;
                        if (parts.length > 12 && !parts[12].trim().isEmpty()) {
                            try {
                                status = Reservation.ReservationStatus.valueOf(parts[12]);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid status in data file: " + parts[12] + ", using PENDING");
                            }
                        }
                        
                        reservations.add(new Reservation(id, name, time, phoneNumber, department, doctor,
                                symptoms, insuranceNumber, emergencyContact, allergies, medications, notes, status)); 
                        if (id > maxId) { 
                            maxId = id; 
                        } 
                    } catch (NumberFormatException e) { 
                        System.err.println("Skipping invalid data file line (NumberFormatException): " +
                                line + " - " + e.getMessage()); 
                    } catch (DateTimeParseException e) { 
                        System.err.println("Skipping invalid data file line (DateTimeParseException): " +
                                line + " - " + e.getMessage()); 
                    } 
                } else if (parts.length == 3) {
                    try { 
                        int id = Integer.parseInt(parts[0]); 
                        String name = parts[1]; 
                        LocalDateTime time = LocalDateTime.parse(parts[2], FORMATTER); 
                        reservations.add(new Reservation(id, name, time)); 
                        if (id > maxId) { 
                            maxId = id; 
                        } 
                    } catch (NumberFormatException e) { 
                        System.err.println("Skipping invalid old format line (NumberFormatException): " +
                                line + " - " + e.getMessage()); 
                    } catch (DateTimeParseException e) { 
                        System.err.println("Skipping invalid old format line (DateTimeParseException): " +
                                line + " - " + e.getMessage()); 
                    } 
                }
            } 
            idCounter.set(maxId); 
        } catch (IOException e) { 
            System.err.println("Error loading reservations (IOException): " + e.getMessage()); 
        } catch (Exception e) { 
            System.err.println("An unexpected error occurred while loading reservations: " + e.getMessage()); 
            e.printStackTrace(); 
        } 
    }

    public boolean updateReservationStatus(int id, Reservation.ReservationStatus status) {
        for (Reservation res : reservations) {
            if (res.getId() == id) {
                res.setStatus(status);
                saveReservations();
                return true;
            }
        }
        return false;
    }
    
    public boolean updateMedicalReservationWithStatus(int id, String name, LocalDateTime reservationTime, String phoneNumber,
                                                    String department, String doctor, String symptoms, String insuranceNumber,
                                                    String emergencyContact, String allergies, String medications, 
                                                    String notes, Reservation.ReservationStatus status) {
        if (isDuplicate(name, reservationTime, id)) { 
            return false; 
        } 
        for (int i = 0; i < reservations.size(); i++) { 
            if (reservations.get(i).getId() == id) { 
                Reservation updated = new Reservation(id, name, reservationTime, phoneNumber, department, doctor,
                                                    symptoms, insuranceNumber, emergencyContact, allergies, medications, notes, status);
                reservations.set(i, updated); 
                saveReservations(); 
                return true; 
            } 
        } 
        return false;
    }
    
    public List<Reservation> getReservationsByStatus(Reservation.ReservationStatus status) {
        return reservations.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void exportToCSV(BufferedWriter writer) throws IOException {
        writer.write("ID,名前,予約日時");
        writer.newLine();
        for (Reservation res : reservations) {
            writer.write(String.format("%d,%s,%s", res.getId(), res.getName(),
                    res.getReservationTime().format(FORMATTER)));
            writer.newLine();
        }
    }
} 