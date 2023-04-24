package com.udacity.jwdnd.course1.cloudstorage.Controller;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/file")
public class FileController {
    private FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload, Authentication authentication, RedirectAttributes redirectAttributes) {
        // fileUpload variable name has to match the name attribute in the input tag for the file upload
        // @RequestParam("fileUpload") is used to check that we receive the `fileUpload` parameter
        if (fileUpload == null || fileUpload.isEmpty()) {
            redirectAttributes.addFlashAttribute("fileError", "Please select a file to upload.");
            return "redirect:/home";
        }

        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String fileError = null;
        String fileName = StringUtils.cleanPath(fileUpload.getOriginalFilename());
        long fileSize = fileUpload.getSize();
        long oneMB = 1024 * 1024;

        // check for errors
        if (fileService.fileExist(userId,fileName)){
            if (fileName.isEmpty()) {
                fileError = "Please 'choose a file' before attempting to upload. ";
                redirectAttributes.addFlashAttribute("fileError", fileError);
            }
            else {
                fileError = "Filename: " + fileName + " is already taken. Try a different name. ";
                redirectAttributes.addFlashAttribute("fileError", fileError);
            }
        } else {
            try {
                // handle successful file upload
                int isFileInserted = fileService.insertFile(new File(
                        null,
                        fileUpload.getOriginalFilename(),
                        fileUpload.getContentType(),
                        String.valueOf(fileSize),
                        userId,
                        fileUpload.getBytes()
                ));

                if (isFileInserted < 0) {
                    fileError = "There was an error uploading file.";
                }
            } catch (MaxUploadSizeExceededException e) {
                fileError = "File size is greater than the limit of 1MB";
                redirectAttributes.addFlashAttribute("fileError", fileError);
                return "redirect:/home";
            } catch (IOException e) {
                fileError = "There was an error uploading file.";
                redirectAttributes.addFlashAttribute("fileError", fileError);
                return "redirect:/home";
            }
        }

        if (fileError == null) {
            redirectAttributes.addFlashAttribute("fileSuccess", "Successfully uploaded file.");
        } else {
            redirectAttributes.addFlashAttribute("fileError", fileError);
        }

        return "redirect:/home";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileId") Integer fileId,Authentication authentication, RedirectAttributes redirectAttributes){
        File file = fileService.getFile(fileId);
        String filename = file.getFilename();
        if (filename == null) {
            redirectAttributes.addFlashAttribute("fileError", "Error downloading file.");
        }

        //Create a builder with the status set to OK.
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getContenttype()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename +"\"")
                .body(file.getFileData());
    }
    @GetMapping("/delete/{fileId}")
    public String deleteFile(@PathVariable("fileId") Integer fileId, RedirectAttributes redirectAttributes,Authentication authentication){
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        File file = fileService.getFile(fileId);
        if (fileService.fileExist(userId,file.getFilename())){
            int requestResponse = fileService.deleteFile(file);


        if (requestResponse < 0) {
            redirectAttributes.addFlashAttribute("fileError", "Error deleting file.");
        }} else {
            redirectAttributes.addFlashAttribute("fileSuccess", "Deleting file successful.");
        }

        return "redirect:/home";
    }
}


