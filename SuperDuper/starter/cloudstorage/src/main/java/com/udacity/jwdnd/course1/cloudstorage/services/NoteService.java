package com.udacity.jwdnd.course1.cloudstorage.services;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }
    public List<Note> getNotes(Integer userId){
        return noteMapper.getNotes(userId);
    }
    public int createNote(Note note){
        return noteMapper.insertNote(new Note(null, note.getNoteTitle(), note.getNoteDescription(), note.getUserId()));
    }
    public int updateNote(Note note) {
        return noteMapper.updateNote(note);
    }

    public int deleteNote(Note note) {
        return noteMapper.deleteNote(note.getNoteId());
    }

}
