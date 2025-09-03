package com.example.reservation; 
 
import java.time.LocalDateTime; 
 
public class Reservation {
    
    public enum ReservationStatus {
        PENDING("予約申込中"),
        CONFIRMED("予約確定"),
        CANCELLED("キャンセル"),
        COMPLETED("診療完了"),
        NO_SHOW("未来院");
        
        private final String displayName;
        
        ReservationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    } 
    private int id; 
    private String name; 
    private LocalDateTime reservationTime;
    
    // 医療クリニック用の追加フィールド
    private String phoneNumber;        // 電話番号
    private String department;         // 診療科（内科、外科、皮膚科など）
    private String doctor;             // 担当医師名
    private String symptoms;           // 症状・主訴
    private String insuranceNumber;    // 保険証番号
    private String emergencyContact;   // 緊急連絡先
    private String allergies;          // アレルギー情報
    private String medications;        // 服薬情報
    private String notes;              // 備考
    private ReservationStatus status;  // 予約状態 
 
    // 基本コンストラクタ（既存のコードとの互換性のため）
    public Reservation(int id, String name, LocalDateTime reservationTime) { 
        this.id = id; 
        this.name = name; 
        this.reservationTime = reservationTime; 
        this.status = ReservationStatus.PENDING;
    }
    
    // 医療クリニック用の拡張コンストラクタ
    public Reservation(int id, String name, LocalDateTime reservationTime, String phoneNumber,
                      String department, String doctor, String symptoms, String insuranceNumber,
                      String emergencyContact, String allergies, String medications, String notes) {
        this.id = id;
        this.name = name;
        this.reservationTime = reservationTime;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.doctor = doctor;
        this.symptoms = symptoms;
        this.insuranceNumber = insuranceNumber;
        this.emergencyContact = emergencyContact;
        this.allergies = allergies;
        this.medications = medications;
        this.notes = notes;
        this.status = ReservationStatus.PENDING;
    }
    
    // ステータス付きコンストラクタ
    public Reservation(int id, String name, LocalDateTime reservationTime, String phoneNumber,
                      String department, String doctor, String symptoms, String insuranceNumber,
                      String emergencyContact, String allergies, String medications, String notes, 
                      ReservationStatus status) {
        this.id = id;
        this.name = name;
        this.reservationTime = reservationTime;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.doctor = doctor;
        this.symptoms = symptoms;
        this.insuranceNumber = insuranceNumber;
        this.emergencyContact = emergencyContact;
        this.allergies = allergies;
        this.medications = medications;
        this.notes = notes;
        this.status = status != null ? status : ReservationStatus.PENDING;
    } 
 
    public int getId() { 
        return id; 
    } 
 
    public String getName() { 
        return name; 
    } 
 
    public LocalDateTime getReservationTime() { 
        return reservationTime; 
    } 

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    // 医療クリニック用フィールドのゲッター・セッター
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status != null ? status : ReservationStatus.PENDING;
    }

    @Override
    public String toString() {
        return "MedicalReservation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reservationTime=" + reservationTime +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", department='" + department + '\'' +
                ", doctor='" + doctor + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", insuranceNumber='" + insuranceNumber + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", allergies='" + allergies + '\'' +
                ", medications='" + medications + '\'' +
                ", notes='" + notes + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reservation that = (Reservation) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}