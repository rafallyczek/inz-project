package kanbancalendar.project.app.file;

import com.opencsv.CSVWriter;
import kanbancalendar.project.app.model.Note;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class Csv {

    private List<Note> notes;

    public Csv(List<Note> notes){
        this.notes = notes;
    }

    public ByteArrayInputStream buildCsv() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);

        CSVWriter csvWriter = new CSVWriter(outputStreamWriter,CSVWriter.DEFAULT_SEPARATOR,CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END);

        List<String[]> lines = new ArrayList<>();
        lines.add(new String[]{"Subject","Start date","Start time","Description"});

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for(Note note : notes){
            String[] line = new String[4];
            line[0] = note.getTitle();
            line[1] = dateFormatter.format(note.getDateTime().toLocalDate());
            line[2] = timeFormatter.format(note.getDateTime().toLocalTime());
            line[3] = note.getContent();
            lines.add(line);
        }

        csvWriter.writeAll(lines);
        csvWriter.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
