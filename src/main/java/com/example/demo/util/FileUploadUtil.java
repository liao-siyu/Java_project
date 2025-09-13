package com.example.demo.util;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    public static String saveFile(Integer analystId, MultipartFile file, String type) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String filename = type + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        
        // 儲存到真正存在的專案根目錄/uploads 路徑
        String uploadDir = System.getProperty("user.dir") + "/uploads/analysts/" + analystId;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new IOException("無法建立目錄：" + uploadDir);
            }
        }

        File destination = new File(dir, filename);
        file.transferTo(destination);

        return "/uploads/analysts/" + analystId + "/" + filename; // 給前端用
    }
}

// public class FileUploadUtil {

//     private static final String UPLOAD_DIR = "uploads/analysts";



//     public static String saveFile(Integer analystId, MultipartFile file, String filename) throws IOException {
//         if (file == null || file.isEmpty()) {
//             return null;
//         }

//         // 建立路徑 uploads/analysts/{analystId}/
//         Path uploadPath = Paths.get(BASE_DIR, analystId.toString());
//         if (!Files.exists(uploadPath)) {
//             Files.createDirectories(uploadPath);
//         }

//         // 完整檔案路徑：e.g. /uploads/analysts/1/profile.jpg
//         String extension = getExtension(file.getOriginalFilename());
//         String fullFilename = filename + "." + extension;
//         Path filePath = uploadPath.resolve(fullFilename);

//         // 刪除舊檔案（避免累積）
//         if (Files.exists(filePath)) {
//             Files.delete(filePath);
//         }

//         // 儲存檔案
//         file.transferTo(filePath.toFile());

//         // 回傳相對路徑（可存進 DB）
//         return "analysts/" + analystId + "/" + fullFilename;
//     }

//     // 副檔名處理
//     private static String getExtension(String originalFilename) {
//         int dotIndex = originalFilename.lastIndexOf(".");
//         return (dotIndex > 0) ? originalFilename.substring(dotIndex + 1) : "jpg";
//     }
// }

// public class FileUploadUtil {

//     // ✅ 儲存目錄改為 src/main/resources/static/uploads/analysts
//     private static final String BASE_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/analysts";

//     /**
//      * 儲存檔案
//      * @param analystId 分析師 ID
//      * @param file 檔案（MultipartFile）
//      * @param filename 儲存的檔名（不含副檔名），如 profile、certificate
//      * @return 前端可用的相對路徑，例如 uploads/analysts/1/profile.jpg
//      * @throws IOException
//      */
//     public static String saveFile(Integer analystId, MultipartFile file, String filename) throws IOException {
//         if (file == null || file.isEmpty()) {
//             return null;
//         }

//         // 建立儲存路徑 uploads/analysts/{id}/
//         Path uploadPath = Paths.get(BASE_DIR, analystId.toString());
//         if (!Files.exists(uploadPath)) {
//             Files.createDirectories(uploadPath); // ✅ 自動建立多層資料夾
//         }

//         // 檔案副檔名（保留原始副檔名）
//         String extension = getExtension(file.getOriginalFilename());
//         String fullFilename = filename + "." + extension;

//         Path filePath = uploadPath.resolve(fullFilename);

//         // 儲存檔案（覆蓋同名檔案）
//         file.transferTo(filePath.toFile());

//         // 回傳前端可用路徑
//         return "uploads/analysts/" + analystId + "/" + fullFilename;
//     }

//     // ✅ 副檔名處理
//     private static String getExtension(String originalFilename) {
//         int dotIndex = originalFilename.lastIndexOf(".");
//         return (dotIndex > 0) ? originalFilename.substring(dotIndex + 1) : "jpg";
//     }
// }
