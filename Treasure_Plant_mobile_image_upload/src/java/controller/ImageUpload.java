/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author manga
 */
@MultipartConfig
@WebServlet(name = "ImageUpload", urlPatterns = {"/ImageUpload"})
public class ImageUpload extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        System.out.println("okkkkk");
        
        String id = req.getParameter("product_id");
        Part image0 = req.getPart("image0");
        String count = req.getParameter("image_count");
        
        String applicationPath = req.getServletContext().getRealPath("");
        File folder = new File(applicationPath + "//images//" + id);
        if (!folder.exists()) {
            folder.mkdir();
        }
        
        File file11 = new File(folder, "image0.png");
        InputStream inputStream1 = image0.getInputStream();
        Files.copy(inputStream1, file11.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        for (int i = 0; Integer.parseInt(count) > i; i++) {
            File file = new File(folder, "image"+(i+1)+".png");
            InputStream inputStream = req.getPart("image"+(i+1)).getInputStream();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }


    }

}
