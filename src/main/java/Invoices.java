import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Invoices {
    public static String createSimplePdf(Request req, Response res) throws FileNotFoundException, DocumentException {
        System.out.println(req.body());
        String filename = req.body();

        Document document = new Document(); // dokument pdf
        String path = "src/main/resources/static/katalog/" + filename + ".pdf"; // lokalizacja zapisu
        PdfWriter.getInstance(document, new FileOutputStream(path));

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk(filename, font); // akapit

        document.add(chunk);
        document.close();

        return filename;
    }

    public static String createCommunalCarsInvoice(Request req, Response res) throws FileNotFoundException, DocumentException {
        Invoice invoice = App.invoices.get(1);

        float sum = 0;

        Document document = new Document(); // dokument pdf
        String path = "src/main/resources/static/katalog/all-Cars-Invoice.pdf"; // lokalizacja zapisu
        PdfWriter.getInstance(document, new FileOutputStream(path));

        document.open();

        Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        Font redFont = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.RED);

        Paragraph paragraph = new Paragraph("FAKTURA: "+ invoice.getTitle(), font);
        document.add(paragraph);

        Paragraph buyer = new Paragraph("kupujacy: " + invoice.getBuyer(), font); // info o sprzedawcy
        document.add(buyer);

        Paragraph seller = new Paragraph("sprzedawca: " + invoice.getSeller(), font); // info o kupującym
        document.add(seller);

        // faktura za wszystkie samochody
        Paragraph description = new Paragraph("Faktura za wszystkie samochody", redFont);
        document.add(description);

        PdfPTable table = new PdfPTable(4);
        for(int i = 1; i <= App.invoices.size(); i++){
            Invoice invoiceToTable = App.invoices.get(i);
            if( i != 1){
                PdfPCell id = new PdfPCell(new Phrase(String.valueOf(i), font));
                table.addCell(id);

                PdfPCell price = new PdfPCell(new Phrase(String.valueOf(invoiceToTable.getPrice()),font));
                table.addCell(price);

                PdfPCell vat = new PdfPCell(new Phrase(String.valueOf(invoiceToTable.getVatRate()) + "%", font));
                table.addCell(vat);

                sum += invoiceToTable.getValue();
                PdfPCell value = new PdfPCell(new Phrase(String.valueOf(invoiceToTable.getValue()),font));
                table.addCell(value);
            } else {
                PdfPCell id = new PdfPCell(new Phrase("lp", font));
                table.addCell(id);

                PdfPCell price = new PdfPCell(new Phrase("cena",font));
                table.addCell(price);

                PdfPCell vat = new PdfPCell(new Phrase("vat", font));
                table.addCell(vat);

                PdfPCell value = new PdfPCell(new Phrase("wartosc", font));
                table.addCell(value);
            }
        }

        document.add(table);

        Paragraph resultSum = new Paragraph("DO ZAPLATY " + sum, font);
        document.add(resultSum);

        document.close();

        return "stworzono fakturę";
    }

    public static String createByYearInvoice(Request req, Response res) throws FileNotFoundException, DocumentException {
        Gson gson = new Gson();
        String year = req.params("year");
        float sum = 0;

        Invoice invoiceTitle = App.invoices.get(1);

        Document document = new Document(); // dokument pdf
        String path = "src/main/resources/static/katalog/by-Year-Invoice.pdf"; // lokalizacja zapisu
        PdfWriter.getInstance(document, new FileOutputStream(path));

        Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        Font redFont = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.RED);

        document.open();

        Paragraph paragraph = new Paragraph("FAKTURA: " + invoiceTitle.getTitle(), font);
        document.add(paragraph);

        Paragraph buyer = new Paragraph("kupujacy: " + invoiceTitle.getBuyer(), font); // info o sprzedawcy
        document.add(buyer);

        Paragraph seller = new Paragraph("sprzedawca: " + invoiceTitle.getSeller(), font); // info o kupującym
        document.add(seller);

        // faktura za wszystkie samochody
        Paragraph description = new Paragraph("Faktura za samochody z roku " + year, redFont);
        document.add(description);

        PdfPTable table = new PdfPTable(4);

        PdfPCell id = new PdfPCell(new Phrase("lp", font));
        table.addCell(id);

        PdfPCell price = new PdfPCell(new Phrase("cena", font));
        table.addCell(price);

        PdfPCell vat = new PdfPCell(new Phrase("vat", font));
        table.addCell(vat);

        PdfPCell value = new PdfPCell(new Phrase("wartosc", font));
        table.addCell(value);

        for (int i = 0; i < App.generatedCars.size(); i++) {
            Car car = gson.fromJson(App.generatedCars.get(i), Car.class);
            if (!String.valueOf(car.year).equals(year)) continue;
            Invoice invoice = App.invoices.get(i + 1);
            id = new PdfPCell(new Phrase(String.valueOf(i), font));
            table.addCell(id);

            price = new PdfPCell(new Phrase(String.valueOf(invoice.getPrice()), font));
            table.addCell(price);

            vat = new PdfPCell(new Phrase(String.valueOf(invoice.getVatRate()) + "%", font));
            table.addCell(vat);

            sum += invoice.getValue();
            value = new PdfPCell(new Phrase(String.valueOf(invoice.getValue()), font));
            table.addCell(value);
        }

        document.add(table);

        Paragraph resultSum = new Paragraph("DO ZAPLATY " + sum, font);
        document.add(resultSum);

        document.close();
        return "stworzono fakturę";
    }

    public static String createByPriceInvoice(Request req, Response res) throws FileNotFoundException, DocumentException{
        Gson gson = new Gson();
        Invoice invoice = App.invoices.get(1);
        float sum = 0;
        String min = req.params("min");
        String max = req.params("max");
        System.out.println(min + " " + max);
        Document document = new Document(); // dokument pdf
        String path = "src/main/resources/static/katalog/by-Price-Invoice.pdf"; // lokalizacja zapisu
        PdfWriter.getInstance(document, new FileOutputStream(path));

        document.open();

        Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        Font redFont = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.RED);

        Paragraph paragraph = new Paragraph("FAKTURA: "+ invoice.getTitle(), font);
        document.add(paragraph);

        Paragraph buyer = new Paragraph("kupujacy: " + invoice.getBuyer(), font); // info o sprzedawcy
        document.add(buyer);

        Paragraph seller = new Paragraph("sprzedawca: " + invoice.getSeller(), font); // info o kupującym
        document.add(seller);

        // faktura za wszystkie samochody
        Paragraph description = new Paragraph("Faktura za wszystkie samochody", redFont);
        document.add(description);

        PdfPTable table = new PdfPTable(4);

        PdfPCell id = new PdfPCell(new Phrase("lp", font));
        table.addCell(id);

        PdfPCell price = new PdfPCell(new Phrase("cena",font));
        table.addCell(price);

        PdfPCell vat = new PdfPCell(new Phrase("vat", font));
        table.addCell(vat);

        PdfPCell value = new PdfPCell(new Phrase("wartosc", font));
        table.addCell(value);
        for(int i = 1; i <= App.generatedCars.size(); i++){
            Car car = gson.fromJson(App.generatedCars.get(i-1), Car.class);
            if (car.price > Integer.parseInt(min) && car.price < Integer.parseInt(max)) {
                Invoice invoiceToTable = App.invoices.get(i);
                id = new PdfPCell(new Phrase(String.valueOf(i), font));
                table.addCell(id);
                price = new PdfPCell(new Phrase(String.valueOf(invoiceToTable.getPrice()), font));
                table.addCell(price);

                vat = new PdfPCell(new Phrase(invoiceToTable.getVatRate() + "%", font));
                table.addCell(vat);

                sum += invoiceToTable.getValue();
                System.out.println(invoiceToTable.getValue());
                value = new PdfPCell(new Phrase(String.valueOf(invoiceToTable.getValue()), font));
                table.addCell(value);
            }
        }

        document.add(table);

        Paragraph resultSum = new Paragraph("DO ZAPLATY " + sum, font);
        document.add(resultSum);

        document.close();

        return "stworzono fakture";
    }

    public static String downloadInvoice(Request req, Response res ) throws IOException {
        String filename = req.params("name");
        System.out.println(filename);

        res.type("application/octet-stream");
        res.header("Content-Disposition", "attachment; filename=" + filename + ".pdf"); // nagłówek
        String path = "src/main/resources/static/katalog/" + filename + ".pdf";
        OutputStream outputStream = res.raw().getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(path))); // response pliku do przeglądarki*/
        return "pobieranie...";
    }
}
