package com.udacity.jwdnd.course1.cloudstorage.Controller;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/note")
public class NoteController {
    private NoteService noteService;
    private final UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @PostMapping()
    public String addOrUpdateNote(@ModelAttribute("Note") Note note, Authentication authentication, RedirectAttributes redirectAttributes,Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String noteError;
        int created;
        if(note.getNoteId() == null) {
            note.setUserId(userId);
            created=noteService.createNote(note);
            if(created<1){
                noteError = "There was an error creating the note.";
                redirectAttributes.addFlashAttribute("noteError", noteError);
                model.addAttribute("ErrorAction", true);

            } else {
                redirectAttributes.addFlashAttribute("noteSuccess", "Successfully created note.");
                model.addAttribute("SuccessAction", true);
            }
            }
        else{
            created = noteService.updateNote(note);
            if (created < 1) {
                noteError = "There was an error updating the note.";
                redirectAttributes.addFlashAttribute("noteError", noteError);
                model.addAttribute("ErrorAction", true);
            } else {
                redirectAttributes.addFlashAttribute("noteSuccess", "Successfully edited note.");
                model.addAttribute("SuccessAction", true);
            }
        }
        return "result";
    }
    @GetMapping("/delete/{noteId}")
    public String deleteNote(@ModelAttribute("Note") Note note, RedirectAttributes redirectAttributes,Model model){
        int deleted = noteService.deleteNote(note);

        if (deleted < 1) {
            redirectAttributes.addFlashAttribute("noteError", "Error deleting note.");
            model.addAttribute("ErrorAction", true);
        } else {
            redirectAttributes.addFlashAttribute("noteSuccess", "Deleting note successful.");
            model.addAttribute("SuccessAction", true);
        }

        return "result";
    }
}


