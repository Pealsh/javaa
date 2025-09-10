package com.example.reservation; 
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
 
 
@MultipartConfig 
public class ReservationServlet extends HttpServlet { 
    private final ReservationDAO reservationDAO = new ReservationDAO(); 
 
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action"); 
 
        if ("list".equals(action) || action == null) { 
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            String searchTerm = req.getParameter("search"); 
            String sortBy = req.getParameter("sortBy"); 
            String sortOrder = req.getParameter("sortOrder"); 
            String message = req.getParameter("message");
            int page = 1; 
            int recordsPerPage = 5; 
 
            if (req.getParameter("page") != null) { 
                page = Integer.parseInt(req.getParameter("page")); 
            }

            
            
            List<Reservation> allReservations = reservationDAO.searchAndSortReservations(searchTerm, sortBy, 
sortOrder); 
 
            // ページ番号の妥当性チェック
            if (page < 1) page = 1;
            
            int totalRecords = allReservations.size();
            int noOfPages = Math.max(1, (int) Math.ceil(totalRecords * 1.0 / recordsPerPage));
            
            // ページ番号が最大ページ数を超えている場合は最大ページに調整
            if (page > noOfPages) page = noOfPages;
            
            int start = Math.max(0, (page - 1) * recordsPerPage); 
            int end = Math.min(start + recordsPerPage, totalRecords); 
            List<Reservation> reservations;
            if (start >= totalRecords || totalRecords == 0) {
                reservations = new ArrayList<>();
            } else {
                reservations = allReservations.subList(start, end);
            } 
 
 
 
            req.setAttribute("reservations", reservations); 
            req.setAttribute("noOfPages", noOfPages); 
            req.setAttribute("currentPage", page); 
            req.setAttribute("searchTerm", searchTerm); 
            req.setAttribute("sortBy", sortBy); 
            req.setAttribute("sortOrder", sortOrder);
            
            // インポート成功メッセージの処理
            if (message != null && !message.trim().isEmpty()) {
                req.setAttribute("successMessage", message);
            } 
 
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/list.jsp"); 
            rd.forward(req, resp); 
        } else if ("edit".equals(action)) { 
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int id = Integer.parseInt(req.getParameter("id")); 
            Reservation reservation = reservationDAO.getReservationById(id); 
            req.setAttribute("reservation", reservation); 
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
            rd.forward(req, resp); 
        } else if ("view".equals(action)) { 
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int id = Integer.parseInt(req.getParameter("id")); 
            Reservation reservation = reservationDAO.getReservationById(id); 
            req.setAttribute("reservation", reservation); 
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/view.jsp"); 
            rd.forward(req, resp); 
        } else if ("export_csv".equals(action)) { 
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            exportCsv(req, resp); 
        } else if ("clean_up".equals(action)) { 
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int deletedCount = reservationDAO.getAllReservations().size();
            reservationDAO.cleanUpPastReservations(); 
            resp.sendRedirect("reservation?action=list&message=" + deletedCount + "件の予約を削除しました"); 
        } else if ("confirm".equals(action)) {
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int id = Integer.parseInt(req.getParameter("id"));
            if (reservationDAO.updateReservationStatus(id, Reservation.ReservationStatus.CONFIRMED)) {
                resp.sendRedirect("reservation?action=list&message=予約を確定しました");
            } else {
                resp.sendRedirect("reservation?action=list&message=予約確定に失敗しました");
            }
        } else if ("cancel".equals(action)) {
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int id = Integer.parseInt(req.getParameter("id"));
            if (reservationDAO.updateReservationStatus(id, Reservation.ReservationStatus.CANCELLED)) {
                resp.sendRedirect("reservation?action=list&message=予約をキャンセルしました");
            } else {
                resp.sendRedirect("reservation?action=list&message=予約キャンセルに失敗しました");
            }
        } else if ("complete".equals(action)) {
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int id = Integer.parseInt(req.getParameter("id"));
            if (reservationDAO.updateReservationStatus(id, Reservation.ReservationStatus.COMPLETED)) {
                resp.sendRedirect("reservation?action=list&message=診療を完了しました");
            } else {
                resp.sendRedirect("reservation?action=list&message=診療完了の設定に失敗しました");
            }
        } else { 
            resp.sendRedirect("index.jsp"); 
        } 
    } 
 
    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
        req.setCharacterEncoding("UTF-8"); 
        String action = req.getParameter("action"); 
 
        if ("list".equals(action)) {
            // POST経由での検索・ソート処理を直接実行
            String searchTerm = req.getParameter("search");
            String sortBy = req.getParameter("sortBy");
            String sortOrder = req.getParameter("sortOrder");
            String message = req.getParameter("message");
            int page = 1;
            int recordsPerPage = 5;

            if (req.getParameter("page") != null) {
                try {
                    page = Integer.parseInt(req.getParameter("page"));
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }


            if (page < 1) page = 1;
            
            List<Reservation> allReservations = reservationDAO.searchAndSortReservations(searchTerm, sortBy, sortOrder);
            
            int totalRecords = allReservations.size();
            int noOfPages = Math.max(1, (int) Math.ceil(totalRecords * 1.0 / recordsPerPage));
            

            if (page > noOfPages) page = noOfPages;
            
            int start = Math.max(0, (page - 1) * recordsPerPage);
            int end = Math.min(start + recordsPerPage, totalRecords);
            
            List<Reservation> reservations;
            if (start >= totalRecords || totalRecords == 0) {
                reservations = new ArrayList<>();
            } else {
                reservations = allReservations.subList(start, end);
            }

            req.setAttribute("reservations", reservations);
            req.setAttribute("noOfPages", noOfPages);
            req.setAttribute("currentPage", page);
            req.setAttribute("searchTerm", searchTerm);
            req.setAttribute("sortBy", sortBy);
            req.setAttribute("sortOrder", sortOrder);
            

            if (message != null && !message.trim().isEmpty()) {
                req.setAttribute("successMessage", message);
            }

            RequestDispatcher rd = req.getRequestDispatcher("/jsp/list.jsp");
            rd.forward(req, resp);
            return;
        } else if ("add".equals(action)) { 
            // 医療クリニック予約用の全パラメータを取得
            String name = req.getParameter("name"); 
            String phoneNumber = req.getParameter("phoneNumber");
            String insuranceNumber = req.getParameter("insuranceNumber");
            String department = req.getParameter("department");
            String doctor = req.getParameter("doctor");
            String reservationTimeString = req.getParameter("reservation_time");
            String symptoms = req.getParameter("symptoms");
            String allergies = req.getParameter("allergies");
            String medications = req.getParameter("medications");
            String emergencyContact = req.getParameter("emergencyContact");
            String notes = req.getParameter("notes");
            
            // 必須フィールドの検証
            if (name == null || name.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "患者名は必須です。"); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "電話番号は必須です。"); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (insuranceNumber == null || insuranceNumber.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "保険証番号は必須です。"); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (department == null || department.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "診療科の選択は必須です。"); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (symptoms == null || symptoms.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "症状・主訴は必須です。"); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp); 
                return; 
            } 
 
            if (reservationTimeString == null || reservationTimeString.isEmpty()) { 
                req.setAttribute("errorMessage", "希望診療日時は必須です。"); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp); 
                return; 
            } 
 
            try { 
                LocalDateTime reservationTime = LocalDateTime.parse(reservationTimeString); 
                if (reservationTime.isBefore(LocalDateTime.now())) { 
                    req.setAttribute("errorMessage", "過去の日時は選択できません。"); 
                    RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                    rd.forward(req, resp); 
                    return; 
                } 
                
                // 医療クリニック用の拡張予約追加メソッドを使用
                if (!reservationDAO.addMedicalReservation(name, reservationTime, phoneNumber,
                        department, doctor, symptoms, insuranceNumber, emergencyContact, 
                        allergies, medications, notes)) { 
                    req.setAttribute("errorMessage", "同じ患者名と日時での診療予約は既に存在します。"); 
                    RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                    rd.forward(req, resp); 
                    return; 
                } 
                // 成功時はトップページに成功メッセージ付きでリダイレクト
                resp.sendRedirect(req.getContextPath() + "/?message=success"); 
            } catch (DateTimeParseException e) { 
                req.setAttribute("errorMessage", "有効な日時を入力してください。"); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp); 
            } 
        } else if ("update".equals(action)) { 
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int id = Integer.parseInt(req.getParameter("id")); 
            
            // 医療クリニック予約用の全パラメータを取得
            String name = req.getParameter("name"); 
            String phoneNumber = req.getParameter("phoneNumber");
            String insuranceNumber = req.getParameter("insuranceNumber");
            String department = req.getParameter("department");
            String doctor = req.getParameter("doctor");
            String reservationTimeString = req.getParameter("reservation_time");
            String symptoms = req.getParameter("symptoms");
            String allergies = req.getParameter("allergies");
            String medications = req.getParameter("medications");
            String emergencyContact = req.getParameter("emergencyContact");
            String notes = req.getParameter("notes");
            String statusStr = req.getParameter("status"); 
 
            // 必須フィールドの検証
            if (name == null || name.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "患者名は必須です。"); 
                Reservation reservation = reservationDAO.getReservationById(id);
                req.setAttribute("reservation", reservation);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "電話番号は必須です。"); 
                Reservation reservation = reservationDAO.getReservationById(id);
                req.setAttribute("reservation", reservation);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (insuranceNumber == null || insuranceNumber.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "保険証番号は必須です。"); 
                Reservation reservation = reservationDAO.getReservationById(id);
                req.setAttribute("reservation", reservation);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (department == null || department.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "診療科の選択は必須です。"); 
                Reservation reservation = reservationDAO.getReservationById(id);
                req.setAttribute("reservation", reservation);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                rd.forward(req, resp); 
                return; 
            }
            
            if (symptoms == null || symptoms.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "症状・主訴は必須です。"); 
                Reservation reservation = reservationDAO.getReservationById(id);
                req.setAttribute("reservation", reservation);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                rd.forward(req, resp); 
                return; 
            }

            if (reservationTimeString == null || reservationTimeString.trim().isEmpty()) { 
                req.setAttribute("errorMessage", "希望診療日時は必須です。"); 
                Reservation reservation = reservationDAO.getReservationById(id);
                req.setAttribute("reservation", reservation);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                rd.forward(req, resp); 
                return; 
            } 
 
            try { 
                LocalDateTime reservationTime = LocalDateTime.parse(reservationTimeString); 
                if (reservationTime.isBefore(LocalDateTime.now())) { 
                    req.setAttribute("errorMessage", "過去の日時は選択できません。"); 
                    Reservation reservation = reservationDAO.getReservationById(id);
                    req.setAttribute("reservation", reservation);
                    RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                    rd.forward(req, resp); 
                    return; 
                } 
                
                // ステータスの解析
                Reservation.ReservationStatus status = Reservation.ReservationStatus.PENDING;
                if (statusStr != null && !statusStr.trim().isEmpty()) {
                    try {
                        status = Reservation.ReservationStatus.valueOf(statusStr);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid status in update: " + statusStr + ", using PENDING");
                    }
                }
                
                // 医療クリニック用の拡張予約更新メソッドを使用
                if (!reservationDAO.updateMedicalReservationWithStatus(id, name, reservationTime, phoneNumber,
                        department, doctor, symptoms, insuranceNumber, emergencyContact, 
                        allergies, medications, notes, status)) { 
                    req.setAttribute("errorMessage", "同じ患者名と日時での診療予約は既に存在します。"); 
                    Reservation reservation = reservationDAO.getReservationById(id);
                    req.setAttribute("reservation", reservation);
                    RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                    rd.forward(req, resp); 
                    return; 
                } 
                resp.sendRedirect("reservation?action=list"); 
            } catch (DateTimeParseException e) { 
                req.setAttribute("errorMessage", "有効な日時を入力してください。"); 
                Reservation reservation = reservationDAO.getReservationById(id);
                req.setAttribute("reservation", reservation);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/edit.jsp"); 
                rd.forward(req, resp); 
            } 
        } else if ("delete".equals(action)) { 
            // 管理者ログインチェック
            if (!isAdminLoggedIn(req)) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            int id = Integer.parseInt(req.getParameter("id")); 
            reservationDAO.deleteReservation(id); 
            resp.sendRedirect("reservation?action=list"); 
        } else if ("import_csv".equals(action)) { 
            try { 
                Part filePart = req.getPart("csvFile"); 
                if (filePart != null && filePart.getSize() > 0) { 
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream(), 
"UTF-8"))) { 
                        reservationDAO.importReservations(reader); 
                        // インポート完了後、予約一覧ページにリダイレクト
                        resp.sendRedirect("reservation?action=list&message=CSV+ファイルのインポートが完了しました"); 
                        return;
                    } 
                } 
                else { 
                    req.setAttribute("errorMessage", "インポートするファイルを選択してください。"); 
                    RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                    rd.forward(req, resp);
                    return;
                }
            } catch (Exception e) { 
                req.setAttribute("errorMessage", "CSV ファイルのインポート中にエラーが発生しました: " + 
e.getMessage()); 
                e.printStackTrace(); 
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp"); 
                rd.forward(req, resp);
                return;
            } 
        } else { 
            resp.sendRedirect("index.jsp"); 
        } 
    } 
 
    private void exportCsv(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
        resp.setContentType("text/csv; charset=UTF-8"); 
        resp.setHeader("Content-Disposition", "attachment; filename=\"medical_reservations.csv\""); 
 
        PrintWriter writer = resp.getWriter(); 
        // 医療クリニック用のCSVヘッダー
        writer.append("患者ID,患者名,電話番号,診療科,担当医師,診療日時,症状・主訴,保険証番号,緊急連絡先,アレルギー,服薬情報,備考,予約状態\n"); 
 
        List<Reservation> records = reservationDAO.getAllReservations(); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"); 
 
        for (Reservation record : records) { 
            writer.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",%s,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", 
                    record.getId(), 
                    record.getName() != null ? record.getName() : "",
                    record.getPhoneNumber() != null ? record.getPhoneNumber() : "",
                    record.getDepartment() != null ? record.getDepartment() : "",
                    record.getDoctor() != null ? record.getDoctor() : "",
                    record.getReservationTime() != null ? record.getReservationTime().format(formatter) : "",
                    record.getSymptoms() != null ? record.getSymptoms().replace("\"", "\\\"") : "",
                    record.getInsuranceNumber() != null ? record.getInsuranceNumber() : "",
                    record.getEmergencyContact() != null ? record.getEmergencyContact() : "",
                    record.getAllergies() != null ? record.getAllergies().replace("\"", "\\\"") : "",
                    record.getMedications() != null ? record.getMedications().replace("\"", "\\\"") : "",
                    record.getNotes() != null ? record.getNotes().replace("\"", "\\\"") : "",
                    record.getStatus() != null ? record.getStatus().getDisplayName() : "予約申込中")); 
        } 
        writer.flush(); 
    }
    
    // 管理者ログインチェックメソッド
    private boolean isAdminLoggedIn(HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            return isAdmin != null && isAdmin;
        }
        return false;
    }
    
} 