package com.example.reservation;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // パスワード定数
    private static final String ADMIN_PASSWORD = "12341234lol";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // ログインページを表示
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String password = request.getParameter("password");
        
        if (password != null && ADMIN_PASSWORD.equals(password.trim())) {
            // パスワードが正しい場合
            HttpSession session = request.getSession();
            session.setAttribute("isAdmin", true);
            session.setAttribute("loginTime", System.currentTimeMillis());
            
            // 予約一覧ページにリダイレクト
            response.sendRedirect(request.getContextPath() + "/reservation?action=list");
        } else {
            // パスワードが間違っている場合
            request.setAttribute("errorMessage", "パスワードが間違っています。");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
}