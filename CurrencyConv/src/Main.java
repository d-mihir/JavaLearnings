import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Scanner;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {

    static double PerformConversion(String code1, String code2, double amount) {
        //performs actual conversion
        Gson gson = new Gson();
        final String accessKey = "access_key";
        final String url = "http://api.currencylayer.com/live?access_key="+accessKey+"&source="+code1+"&currencies="+code2;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .method("GET",HttpRequest.BodyPublishers.noBody()).build();


        HttpResponse<String> response = null;
        double convFactor = 0.0;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                java.util.Map<String, Object> parsedData = gson.fromJson(response.body(), java.util.Map.class);
                java.util.Map<String, Double> convMap = (java.util.Map) parsedData.get("quotes");
                convFactor = convMap.get("" + code1 + "" + code2);
            } else {
                System.out.println("API Failure try after sometime");
            }
        }
        catch (Exception e){
        e.printStackTrace();
        }
        return convFactor * amount;
    }
    static boolean ValidateUserOption(String[] codes, String CurrencyCode) {
        //user chooses two codes and that must be in the list
        return Arrays.asList(codes).contains(CurrencyCode) && Arrays.asList(codes).contains(CurrencyCode);
    }

    static String[] GetCurrencyOptions() {
        //get currencies from the api site and display it and store it in a string.
        final String url = "https://currencylayer.com/currencies";
        String[] currencyCodes = null;
        try {
            final Document document = Jsoup.connect(url).get();
            for( Element row: document.select("table.currencies")) {
                if (row.select("td:nth-of-type(1)").text().isEmpty()) continue;
                else {
                    String s = row.select("td:nth-of-type(1)").text();
                    currencyCodes = s.split(" ");

                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        int counter = 1;
        System.out.println("The following currency conversions are available!");
        for(String CurrencyCode : currencyCodes){
            System.out.println(counter+". "+CurrencyCode);
            counter+=1;
        }
        return currencyCodes;
    }

    static boolean TakeUserInput(String[] Codes) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the currency code which you want to convert: ");
        String codeA = scanner.next().trim().toUpperCase();

        System.out.print("\nEnter the currency code into which you want to retrieve: ");
        String codeB = scanner.next().trim().toUpperCase();
        double ans = 0.0;
        if(ValidateUserOption(Codes, codeA) && ValidateUserOption(Codes, codeB)){
            double amount = 0.0;
            System.out.print("\nEnter the amount to be converted: ");
            if(amount<0.0) return false;
            amount = scanner.nextDouble();
            ans = PerformConversion(codeA, codeB, amount);
            System.out.println("Amount in "+ codeB + " is "+ans);
            return true;
        }
        else return false;
    }
    static void CurrencyConverter() {
        String[] currencyCodes = GetCurrencyOptions();
		if(TakeUserInput(currencyCodes)){

        }
        else{
            System.out.println("Invalid inputs entered");
        }
    }

    public static void main(String[] args) {

        /*
         * ask user if they want to proceed
         */
        Scanner scanner = new Scanner(System.in);
        String choice;
        System.out.println("Welcome to currency converter!");
		while (true) {
			System.out.print("Do you want to continue: ");
			choice = scanner.next();
			if (choice.equalsIgnoreCase("Yes")) {
				System.out.println("Wait....");
                CurrencyConverter();
			} else if (choice.equalsIgnoreCase("No")) {
				System.out.println("End, thank you!");
				break;
			} else {
				System.out.println("Enter a valid option!");
			}
		}
        scanner.close();
    }

}
