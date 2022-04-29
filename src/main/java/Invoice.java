import java.util.ArrayList;
import java.util.Date;

public class Invoice {
    // elementy tabelki
    private int id;
    private int price;
    private int vatRate;
    private double value;

    // elementy opisu faktury
    private String title;
    private Date time = new Date();
    private String seller;
    private String buyer;
    private ArrayList<String> list;

    public Invoice(int id, int price, int vatRate, String seller, String buyer, ArrayList<String> list) {
        this.id = id;
        this.price = price;
        this.vatRate = vatRate;
        this.seller = seller;
        this.buyer = buyer;
        this.list = list;
        this.title = getInvoiceNumber();
        this.value = setValue();
    }

    public String getInvoiceNumber(){
        // tworzenie numeru faktury o wzorze VAT/rok/miesiac/dzien/timestamp/timestamp
        int minutes = this.time.getMinutes();
        int seconds = this.time.getSeconds();
        int day = this.time.getDay();
        int month = this.time.getMonth();
        int year = this.time.getYear();
        return  "VAT/" + year + "/" + month + "/" + day + "/" + minutes + "/" + seconds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVatRate() {
        return vatRate;
    }

    public void setVatRate(int vatRate) {
        this.vatRate = vatRate;
    }

    public double getValue() {
        return value;
    }

    public double setValue() {
        return Helpers.getPriceWithVat(this.price, this.vatRate);
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
