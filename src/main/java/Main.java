import com.opencsv.CSVReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.*;

public class Main {

    private static final String apiKey = "4219a67c-197e-4df3-979d-52f962fc56b5";

    public static void main(String[] args) throws InterruptedException {
        Map<String, CC> cryptoList = getDataFromAPI();
        displayCurrentPrice(cryptoList);
        ArrayList<Portfolio> portfolios = new ArrayList<>();
        fillThePortfolio(portfolios, cryptoList);

        Scanner in = new Scanner(System.in);
        int userOption;
        displayMenu();
        System.out.print("Please Enter Your Option: ");
        userOption =  in.nextInt();
        System.out.println();
        while (userOption != -1) {
            switch (userOption) {
                case 0:
                    displayMenu();
                    break;
                case 1:
                    Portfolio temp = new Portfolio();
                    in.nextLine();
                    System.out.print("Crypto Symbol: ");
                    temp.setCrypto(lookupCryptoBySym(in.nextLine(), cryptoList));

                    System.out.print("Volumn: ");
                    temp.setVolume(in.nextBigDecimal());

                    System.out.print("Purchase Price(per coin): ");
                    temp.setInitialpricePerCoin(in.nextBigDecimal());

                    portfolios.add(temp);
                    break;
                case 2:
                    if (!cryptoList.isEmpty()) {
                        System.out.print("Getting current price....");
                        Thread.sleep(25000); //data will update 5mint later
                        updateThePrice(cryptoList);
                        displayCurrentPrice(cryptoList);
                    } else System.out.print("Currently No data is Available");
                    break;
                case 3:
                    TextBreak tb = new TextBreak();
                    tb.dash(40);  // With length argument
                    System.out.println(String.format("%-10s %-10s %10s", "SYMBOL", "Volume", "Purchase Price"));
                    tb.dash(40);  // With length argument
                    for(Portfolio i: portfolios){
                        System.out.println(i);}
                    break;
                case 4:
                    printAllPnL(portfolios, cryptoList);
                    break;
                default:
                    System.out.print("Option didn't Match Please try again or Press -1 for exit");
                    userOption =  in.nextInt();
                    System.out.println();


            }
            System.out.print("What Next You Like To Do? ");
            userOption =  in.nextInt();
            System.out.println();
        }
    }
    private static void printAllPnL(ArrayList<Portfolio> portfolios, Map<String, CC> cryptoList){
        TextBreak tb = new TextBreak();
        BigDecimal current, initial, pnl, totalPnL,
                totalInitial=new BigDecimal(0),
                totalCurrent =new BigDecimal(0);

        tb.dash(65);  // With length argument
        System.out.println(String.format("%-10s %-10s %10s %20s", "SYMBOL", "Volume", "Purchase Price", "P&L"));
        tb.dash(65);  // With length argument

        for(Portfolio i: portfolios) {
            initial = i.initalTotalAssetValue();
            current = i.getCrypto().getPrice().multiply(i.getVolume());
            pnl = current.subtract(initial);
            totalInitial = totalInitial.add(initial); totalCurrent = totalCurrent.add(current);
            System.out.println(String.format("%s %30s",i, pnl.toString()
                    + "(" + pnl.divide(initial,2, RoundingMode.FLOOR) +"%)"));
        }
        totalPnL = totalCurrent.subtract(totalInitial);
        BigDecimal totalPnler = new BigDecimal(String.valueOf(totalPnL.divide(totalInitial,2, RoundingMode.HALF_UP)));
        tb.dash(65);  // With length argument
        System.out.println("Overall Invested: $" + totalInitial);
        System.out.println("Overall Value: $" + totalCurrent);
        System.out.println("Overall P&L: $" + totalPnL+"("+totalPnler+"%)");
        tb.dash(65);  // With length argument
    }
    private static void fillThePortfolio(ArrayList<Portfolio> portfolios, Map<String, CC> cryptoList) {
        try {
            String fileName = "data.csv";
            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(fileName);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                int i =0;
                Portfolio temp = new Portfolio(
                cryptoList.get(nextRecord[i]),
                        new BigDecimal(nextRecord[++i]),
                        new BigDecimal(nextRecord[++i]));

                portfolios.add(temp);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static CC lookupCryptoBySym(String sym, Map<String, CC> cryptoList){
        return cryptoList.get(sym);
    }

    public static Map<String, CC> getDataFromAPI(){
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        paratmers.add(new BasicNameValuePair("start","1"));
        paratmers.add(new BasicNameValuePair("limit","5"));
        paratmers.add(new BasicNameValuePair("convert","USD"));
        Map<String, CC> cryptoList = new LinkedHashMap<String,CC>();
        try {
            String result = makeAPICall(uri, paratmers);
            JSONObject obj = new JSONObject(result);
            JSONArray jsonCoinData = obj.getJSONArray("data"); // notice that `"data": [...]`

            //initializing the CC arryList and then display the prices
            fillTheArray(cryptoList, jsonCoinData);

        } catch (IOException e) {
            System.out.println("Error: cannont access content - " + e);
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e);
        }

        return cryptoList;
    }
    public static String makeAPICall(String uri, List<NameValuePair> parameters)
            throws URISyntaxException, IOException {
        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        try (CloseableHttpResponse response = client.execute(request)) {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }

        return response_content;
    }

    public static void fillTheArray (Map<String, CC> cryptoList, JSONArray jsonCoinData){
        for (int i = 0; i < jsonCoinData.length(); i++)
        {
            CC temp = new CC();
            temp.setName(jsonCoinData.getJSONObject(i).getString("name"));
            temp.setTicker(jsonCoinData.getJSONObject(i).getString("symbol"));
            temp.setRank(jsonCoinData.getJSONObject(i).getInt("id"));
            temp.setPrice(jsonCoinData.getJSONObject(i).getJSONObject("quote").getJSONObject("USD").getBigDecimal("price"));
            cryptoList.put(temp.getTicker(), temp);
        }
    }

    public static void updateThePrice (Map<String, CC> cryptoList){
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        List<NameValuePair> paratmers = new ArrayList<>();
        paratmers.add(new BasicNameValuePair("start","1"));
        paratmers.add(new BasicNameValuePair("limit","5"));
        paratmers.add(new BasicNameValuePair("convert","USD"));

        BigDecimal newPrice;
        String sym;
        try {
            String result = makeAPICall(uri, paratmers);
            JSONObject obj = new JSONObject(result);
            JSONArray jsonCoinData = obj.getJSONArray("data"); // notice that `"data": [...]`

            for (int i = 0; i < jsonCoinData.length(); i++)
            {
                newPrice = jsonCoinData.getJSONObject(i).getJSONObject("quote").getJSONObject("USD").getBigDecimal("price");
                sym = jsonCoinData.getJSONObject(i).getString("symbol");
                cryptoList.get(sym).setPrice(newPrice);
            }

        } catch (IOException e) {
            System.out.println("Error: cannont access content");
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL ");
        }

    }

    public static void displayCurrentPrice (Map<String, CC> cryptoList){
        for (String s: cryptoList.keySet()){
            System.out.print("[" + s + ":" + "$" + cryptoList.get(s).getPrice() +"]\t" );
        }
        System.out.println();
    }

    public static void displayMenu(){
        System.out.println("0:\tDisplay menu");
            System.out.println("1:\tAdd new Asset");
            System.out.println("2:\tCheck Market prices"); //Update the price and Display
            System.out.println("3:\tDisplay All Asset");
            System.out.println("4:\tShow total P&L");
            System.out.println("-1:\tExit");

    }
}