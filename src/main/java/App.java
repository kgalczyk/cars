import static spark.Spark.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import com.fasterxml.uuid.Generators;

public class App {
    public static void main(String[] args) {
        staticFiles.location("/static");
        port(4000);

        post("/add",(req, res) -> addCar(req, res));
        post("/json",(req, res) -> sendArrayOfCars(req, res));
        post("/delete",(req, res) -> deleteCar(req, res));
        post("/update", (req, res) -> updateRecord(req, res));
        post("/generate", (req, res) -> generateCars(req, res));
        post("/invoice", (req, res) -> Invoices.createSimplePdf(req, res));
        post("/search", (req,res) -> generateCars(req, res));
        post("/invoice/all", (req, res)-> Invoices.createCommunalCarsInvoice(req, res));
        get("/invoices/:name", (req, res)-> Invoices.downloadInvoice(req, res));
        post("/invoice/:year", (req, res)-> Invoices.createByYearInvoice(req, res));
        post("/invoice/price/:min/:max", (req, res)-> Invoices.createByPriceInvoice(req, res));
    }

    static int carsCounter = 1;

    static ArrayList<String> cars = new ArrayList<>();
    static ArrayList<String> generatedCars = new ArrayList<>();
    static ArrayList<String> models = Helpers.addModelNames();
    static ArrayList<String> airbagsLocalizations = Helpers.addAirbagsLocalizations();

    static HashMap<Integer, Invoice> invoices = new HashMap<>();


    static String addCar(Request req,Response res){
        Gson gson = new Gson();
        Car car = gson.fromJson(req.body(), Car.class);
        UUID uuid = Generators.randomBasedGenerator().generate();
        car.uuid = uuid;
        car.id = carsCounter++;
        cars.add(gson.toJson(car));
        System.out.println(cars);
        res.type("application/json");
        return gson.toJson(car);
    }

    static String deleteCar(Request req, Response res){
        Gson gson = new Gson();
        Id id = gson.fromJson(req.body(), Id.class);
        System.out.println(id.id);
        cars.remove(Integer.parseInt(id.id)-1);
        return id.id;
    }

    static String updateRecord(Request req, Response res){
        Gson gson = new Gson();
        System.out.println(req.body());
        EditedCar editedCar = gson.fromJson(req.body(), EditedCar.class);
        Car car = gson.fromJson(cars.get(editedCar.id-1), Car.class);
        if (editedCar.id == car.id){
            car.model = editedCar.model;
            car.year = editedCar.year;
        }

        cars.set(editedCar.id-1, gson.toJson(car));
        System.out.println(cars);
        return gson.toJson(cars);
    }

    static String sendArrayOfCars(Request req, Response res){
        Gson gson = new Gson();
        res.type("application/json");
        return gson.toJson(cars);
    }

    static String generateCars(Request req, Response res){
        Gson gson = new Gson();
        for (int i = 0; i < 10; i++){
            // id samochodu
            int id = generatedCars.size()+1;

            // uuid
            UUID uuid =  Generators.randomBasedGenerator().generate();

            // losowo wybrany z czterech modeli
            int index = (int) Math.floor(Math.random()*models.size());
            String model = models.get(index);

            // rok losowy z przedziały 1 do 2022
            int year = (int) Math.floor(Math.random()*70 + 1950);

            // kolor
            int red = (int) Math.round(Math.random()*256);
            int green = (int) Math.round(Math.random()*256);
            int blue = (int) Math.round(Math.random()*256);
            String color = Helpers.getColor(red, green, blue);

            // airbagsy
            ArrayList<Airbag> airbags = new ArrayList<>();
            boolean areAirbags = false;
            for (int j = 0; j < 4; j++){
                int k = (int) Math.round(Math.random());
                areAirbags = k != 0;
                Airbag airbag = new Airbag(airbagsLocalizations.get(j), areAirbags);
                airbags.add(airbag);
            }
            int carPrice = Helpers.getRandomCarPrice();
            int vatRate = Helpers.getRandomVATrate();
            Car car = new Car(model ,year, color, airbags, Helpers.getRandomDate(), carPrice, vatRate );
            car.id = id;
            car.uuid = uuid;

            generatedCars.add(gson.toJson(car));

            Invoice invoice = createInvoiceObject(carPrice, vatRate, "sprzedawca", "nabywca");
            invoices.put(generatedCars.size(), invoice);
        }

        return gson.toJson(generatedCars);
    }

    static Invoice createInvoiceObject(int price, int vatRate, String seller, String buyer){
        return new Invoice(invoices.size()+1, price, vatRate, seller, buyer, generatedCars);
    }
}

class Car {
    int id;
    UUID uuid;
    String model;
    int year;
    String color;
    ArrayList<Airbag> airbags;
    boolean isInvoiceGenerated = false;
    CustomDate date;
    int price;
    int VATrate;


    public Car(String model, int year, String color, ArrayList<Airbag> airbags, CustomDate date, int price, int VATrate) {
        this.model = model;
        this.year = year;
        this.color = color;
        this.airbags = airbags;
        this.date = date;
        this.price = price;
        this.VATrate = VATrate;
    }
}

// klasa będzie przechowywać info o poduszkach
class Airbag {
    String description;
    boolean value;
    public Airbag(String description, boolean value) {
        this.description = description;
        this.value = value;
    }
}

class Id{
    String id;
    public Id(String id) {
        this.id = id;
    }
}

class EditedCar{
    int id;
    String model;
    int year;

    public EditedCar(int id, String model, int year) {
        this.id = id;
        this.model = model;
        this.year = year;
    }
}
