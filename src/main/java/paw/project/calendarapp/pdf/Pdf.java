package paw.project.calendarapp.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.Data;
import paw.project.calendarapp.model.Note;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Pdf {

    private List<Note> notes;

    public Pdf(List<Note> notes){
        this.notes = notes;
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
        //Dodaj Tytuł
        paragraph.add(title);
        //Pusta linia
        paragraph.add(new Paragraph(" "));
        //Dodaj paragraf
        document.add(paragraph);

        //Tworzenie tabeli
        PdfPTable table = new PdfPTable(3);
        //Tworzenie nagłówków tabeli
        Paragraph paragraph1 = new Paragraph("Data",new Font(baseFont, 12, Font.BOLD));
        Paragraph paragraph2 = new Paragraph("Wydarzenie",new Font(baseFont, 12, Font.BOLD));
        Paragraph paragraph3 = new Paragraph("Opis",new Font(baseFont, 12, Font.BOLD));
        //Tworzenie komórek nagłówków
        PdfPCell cell1 = new PdfPCell(paragraph1);
        PdfPCell cell2 = new PdfPCell(paragraph2);
        PdfPCell cell3 = new PdfPCell(paragraph3);
        //Wyśrodkuj tekst w komórkach
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        //Dodanie nagłówków do tabeli
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);

        //Dodawanie notek
        for (Note note : notes) {
            cell1 = new PdfPCell(new Paragraph(note.getDate().toString(), new Font(baseFont, 12, Font.NORMAL)));
            cell2 = new PdfPCell(new Paragraph(note.getTitle(), new Font(baseFont, 12, Font.NORMAL)));
            cell3 = new PdfPCell(new Paragraph(note.getContent(), new Font(baseFont, 12, Font.NORMAL)));
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
        }

        //Dodaj tabelę do dokumentu
        document.add(table);

        //Zamknij dokument
        document.close();

        //Zpisz tablicę strumienia wyjściowego do strumienia wejsciowego i zwróć go
        return new ByteArrayInputStream(out.toByteArray());
    }

}
