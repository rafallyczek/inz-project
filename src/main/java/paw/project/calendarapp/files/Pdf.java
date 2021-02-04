package paw.project.calendarapp.files;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.Data;
import paw.project.calendarapp.model.Note;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class Pdf {

    private List<Note> notes;
    private String timezone;

    public Pdf(List<Note> notes, String timezone){
        this.notes = notes;
        this.timezone = timezone;
    }

    public ByteArrayInputStream buildPdf() throws DocumentException, IOException {

        //Utwórz strumień wyjściowy
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //Utwórz dokument
        Document document = new Document();
        //Pisz do strumienia wyjściowego
        PdfWriter.getInstance(document,out);

        //Otwórz dokument
        document.open();

        //Utwórz paragraf
        Paragraph paragraph = new Paragraph();
        //Utwórz czcionkę
        BaseFont baseFont = BaseFont.createFont("Lato-Regular.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
        //Utwórz tytuł
        Paragraph title = new Paragraph("NOTKI",new Font(baseFont, 18, Font.BOLD));
        //Wyrównaj do środka
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        //Dodaj Tytuł
        paragraph.add(title);
        //Dodaj paragraf
        document.add(paragraph);

        //Info
        Paragraph noteCount = new Paragraph("Ilość notek: "+notes.size(),new Font(baseFont, 10, Font.NORMAL));
        document.add(noteCount);
        Paragraph timezonePar = new Paragraph("Strefa czasowa: "+timezone,new Font(baseFont, 10, Font.NORMAL));
        document.add(timezonePar);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timezone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Paragraph created = new Paragraph("Pobrano: "+now.toLocalDate().toString()+", o godzinie: "+now.toLocalTime().format(formatter),new Font(baseFont, 10, Font.NORMAL));
        created.setSpacingAfter(10);
        document.add(created);

        //Tworzenie tabeli
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(90);
        //Tworzenie nagłówków tabeli
        Paragraph paragraph1 = new Paragraph("Data",new Font(baseFont, 12, Font.BOLD));
        Paragraph paragraph2 = new Paragraph("Godzina",new Font(baseFont, 12, Font.BOLD));
        Paragraph paragraph3 = new Paragraph("Wydarzenie",new Font(baseFont, 12, Font.BOLD));
        Paragraph paragraph4 = new Paragraph("Opis",new Font(baseFont, 12, Font.BOLD));
        Paragraph paragraph5 = new Paragraph("Typ",new Font(baseFont, 12, Font.BOLD));
        //Tworzenie komórek nagłówków
        PdfPCell cell1 = new PdfPCell(paragraph1);
        cell1.setPadding(5);
        cell1.setBackgroundColor(new BaseColor(128,128,128));
        PdfPCell cell2 = new PdfPCell(paragraph2);
        cell2.setPadding(5);
        cell2.setBackgroundColor(new BaseColor(128,128,128));
        PdfPCell cell3 = new PdfPCell(paragraph3);
        cell3.setPadding(5);
        cell3.setBackgroundColor(new BaseColor(128,128,128));
        PdfPCell cell4 = new PdfPCell(paragraph4);
        cell4.setPadding(5);
        cell4.setBackgroundColor(new BaseColor(128,128,128));
        PdfPCell cell5 = new PdfPCell(paragraph5);
        cell5.setPadding(5);
        cell5.setBackgroundColor(new BaseColor(128,128,128));
        //Wyśrodkuj tekst w komórkach
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        //Dodanie nagłówków do tabeli
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);

        //Dodawanie notek
        for (Note note : notes) {
            cell1 = new PdfPCell(new Paragraph(note.getDate().toString(), new Font(baseFont, 12, Font.NORMAL)));
            cell1.setBackgroundColor(new BaseColor(169,169,169));
            cell2 = new PdfPCell(new Paragraph(note.getTime(), new Font(baseFont, 12, Font.NORMAL)));
            cell2.setBackgroundColor(new BaseColor(169,169,169));
            cell3 = new PdfPCell(new Paragraph(note.getTitle(), new Font(baseFont, 12, Font.NORMAL)));
            cell3.setBackgroundColor(new BaseColor(169,169,169));
            cell4 = new PdfPCell(new Paragraph(note.getContent(), new Font(baseFont, 12, Font.NORMAL)));
            cell4.setBackgroundColor(new BaseColor(169,169,169));
            if(note.isTask()){
                cell5 = new PdfPCell(new Paragraph("Zadanie", new Font(baseFont, 12, Font.NORMAL, new BaseColor(178,34,34))));
                cell5.setBackgroundColor(new BaseColor(169,169,169));
            }else{
                cell5 = new PdfPCell(new Paragraph("Zwykła", new Font(baseFont, 12, Font.NORMAL)));
                cell5.setBackgroundColor(new BaseColor(169,169,169));
            }
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
        }

        //Dodaj tabelę do dokumentu
        document.add(table);

        //Zamknij dokument
        document.close();

        //Zpisz tablicę strumienia wyjściowego do strumienia wejsciowego i zwróć go
        return new ByteArrayInputStream(out.toByteArray());
    }

}
