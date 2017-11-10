import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Danilo Morgado
 */
public class StatisticGenerator {
    
    /**
     * METHOD: generate command out for the given list
     *
     * @param list with unknown type
     */
    public static <T> void showListElements(List<T> list){
        if (list == null) return;
        for (T t : list) {
            System.out.format("%s%n", t.toString());
        }
    }
    
    /**
     * METHOD: generate command out for the given map
     *
     * @param map as hashmap
     */
    public static void showMapElements(Map<String, Integer> map){
        if (map == null) return;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.format("%06d\t%s%n", entry.getValue(), entry.getKey());
        }
    }
        
    /**
     * METHOD: generate avarage of given number list
     *
     * @param list as numberlist
     */
    private static Double avarage(List<Integer> list) {
        Double d = 0.0;
        for (Integer i : list){
            d += i;
        }
        if (!list.isEmpty()) d = d / list.size();
        
        return d;
    }
    
    /**
     * METHOD: generate standard deviation of given number list and avarage
     *
     * @param list as numberlist
     * @param avarage as double
     */
    private static Double standardDeviation(List<Integer> list, Double avarage) {
        Double x = 0.0;
        for (Integer i : list){
            x += Math.pow(i-avarage, 2.0);
        }
        if ((list.size() - 1) != 0) x = Math.sqrt(x / (list.size() - 1));
        return x;
    }
    
    /**
     * METHOD: clean stringlist for the given format
     *
     * @param list as numberlist
     */
    private static Object[] cleanList(List<String> list) {
        List<String> cleaned = new LinkedList();
        List<Integer> numbers = new LinkedList();
        
        for (String s : list){
            String[] split = s.split("\\s+");
            if (split.length > 1){
                try {
                    Integer i = Integer.parseInt(split[0]);
                    cleaned.add(s);
                    numbers.add(i);
                } catch (NumberFormatException ex){}
            }
        }
        
        return new Object[]{cleaned, numbers};
    }

    public static void main(String[] args)
    {
    	List<String> input = convertBufferedReaderToList(new BufferedReader(new InputStreamReader(System.in)), 0);
    	
    	getUrlStatisticList(input).stream()
    		.map(s -> "\n"+ s +"\n")
    		.forEach(System.out::println);
    }
    
    /**
     * METHOD: generate the outlier list of given file
     *
     * @param file with numberlist
     * @return outlierlist
     */
    public static List<String> getOutlierList(File file) {
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException | NullPointerException ex) { }
        
        List<String> list = convertBufferedReaderToList(br, Integer.MAX_VALUE);
        
        return getOutlierList(list);
    }
    
    /**
     * METHOD: generate the outlier list of given number list
     *
     * @param list as numberlist
     * @return outlierlist
     */
    public static List<String> getOutlierList(List<String> list) {
        List<String> outliers = new LinkedList<>();
        
        if (list == null || list.isEmpty()) return outliers;

        Object[] obj = cleanList(list);
        list = (List<String>) obj[0];
        List<Integer> numbers = (List<Integer>) obj[1];
        
        double avarage = avarage(numbers);
        double standardDeviation = standardDeviation(numbers, avarage);
        
        for (String s : list){
            String[] split = s.split("\\s+");
            Integer num = Integer.parseInt(split[0]);
            if (Math.abs(num - avarage) > (2 * standardDeviation)) {
                outliers.add(s);
            }
        }
        
        return outliers;
    }
    
    /**
     * METHOD: generate numberlist of given map
     *
     * @param map as hashmap
     * @return list with "num url 0...n"
     */
    protected static List<String> getNumberList(Map<String, Integer> map){
        List<String> list = new LinkedList();
        
        if (map == null) return list;
        
        Map<Integer, List<String>> int_map = new HashMap();
        
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (int_map.containsKey(entry.getValue())) {
                int_map.get(entry.getValue()).add(entry.getKey());
            } else {
                List<String> string_list = new LinkedList();
                string_list.add(entry.getKey());
                int_map.put(entry.getValue(), string_list);
            }
        }
        
        for (Map.Entry<Integer, List<String>> entry : int_map.entrySet()) {
            String strg = entry.getKey() + "";
            
            for (String s : entry.getValue()){
                strg = strg + " " + s;
            }
            
            list.add(strg);
        }
        
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String[] split_s1 = s1.split("\\s+");
                String[] split_s2 = s2.split("\\s+");
                Integer i1 = Integer.parseInt(split_s1[0]);
                Integer i2 = Integer.parseInt(split_s2[0]);
                return Integer.compare(i2, i1);
            }
        });
        
        return list;
    }
    
    /**
     * METHOD: merge the second given hashmap in the first given hashmap
     *
     * @param main as hashmap
     * @param map as hashmap
     */
    protected static void mergeMaps(Map<String, Integer> main, Map<String, Integer> map){
        if (main == null || map == null || main.equals(map)) return;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            createOrUpdateMap(main, entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * METHOD: generate the statistic as list of given file with url position of 0
     *
     * @param file as file
     * @return list as numberlist
     */
    public static List<String> getUrlStatisticList(File file){
        return getNumberList(getUrlStatisticMap(file, null));
    }
    
    /**
     * METHOD: generate the statistic as list of given file with given url
     *
     * @param file as file
     * @param position as integer
     * @return list as numberlist
     */
    public static List<String> getUrlStatisticList(File file, Integer position){
        return getNumberList(getUrlStatisticMap(file, position));
    }
    
    /**
     * METHOD: generate the statistic as list of given list
     *
     * @param list with unknown type
     * @return list as numberlist
     */
    public static <T> List<String> getUrlStatisticList(List<T> list){
        return getNumberList(getUrlStatisticMap(list));
    }
    
    /**
     * METHOD: generate the statistic as list of given string
     *
     * @param strg of url
     * @return list as numberlist
     */
    public static List<String> getUrlStatisticList(String strg){
        return getNumberList(getUrlStatisticMap(strg));
    }
    
    /**
     * METHOD: generate the statistic as list of given string
     *
     * @param url
     * @return list as numberlist
     */
    public static List<String> getUrlStatisticList(URL url){
        return getNumberList(getUrlStatisticMap(url));
    }
    
    /**
     * METHOD: generate the statistic as map of given file with url position of 0
     *
     * @param file as file
     * @return map as hashmap
     */
    protected static Map<String, Integer> getUrlStatisticMap(File file){
        return getUrlStatisticMap(file, null);
    }
    
    /**
     * METHOD: generate the statistic as map of given file with url position
     *
     * @param file as file
     * @param position of the url
     * @return map as hashmap
     */
    protected static Map<String, Integer> getUrlStatisticMap(File file, Integer position){
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException | NullPointerException ex) { }
        
        List<String> list = convertBufferedReaderToList(br, position);
        
        return getUrlStatisticMap(list);
    }
    
    /**
     * METHOD: generate the statistic as map of given file with url position
     *
     * @param br as bufferedreader
     * @param position of the url
     * @return strings as list
     */
    protected static List<String> convertBufferedReaderToList(BufferedReader br, Integer position){
        List<String> list = new LinkedList();
        
        if (br == null) return list;
        
        if (position == null) position = 0;
        
        try {
            
            String currentLine;
            
            while ((currentLine = br.readLine()) != null) {
                
                // remove first and end whitespaces
                currentLine = currentLine.trim();
                
                // delete to small sentences for short searching
                if (currentLine.length() < 10) continue;
                
                if(position == Integer.MAX_VALUE){
                    list.add(currentLine);
                } else {
                    String[] split = currentLine.split("\\s+");
                    if (position >= 0 && position < split.length) list.add(split[position]);
                }
            }
            
            br.close();
            
        } catch (FileNotFoundException ex) {
            return list;
        } catch (IOException ex) {
            return list;
        }
        
        return list;
    }
    
    /**
     * METHOD: generate the statistic as map of given list
     *
     * @param list with unknown type
     * @return map as hashmap
     */
    protected static <T> Map<String, Integer> getUrlStatisticMap(List<T> list){
        Map<String, Integer> map = new HashMap<>();
        
        for (T tmp : list){
            Map<String, Integer> tmap = getUrlStatisticMap(tmp.toString());
            mergeMaps(map, tmap);
        }
        
        return map;
    }
    
    /**
     * METHOD: generate the statistic as map of given string
     *
     * @param strg as string
     * @return map as hashmap
     */
    protected static Map<String, Integer> getUrlStatisticMap(String strg){
        URL url = getUrl(strg);
        return getUrlStatisticMap(url);
    }
    
    /**
     * METHOD: generate the statistic as map of given url
     *
     * @param url as url
     * @return map as hashmap
     */
    protected static Map<String, Integer> getUrlStatisticMap(URL url){
        Map<String, Integer> map = new HashMap<>();
        if (url == null) return map;
        
        String[] split = splitUrl(url);
        String adresse = split[0] + "://" + split[1];
        createOrUpdateMap(map, adresse);
        
        String[] paths = split[2].split("/");
        
        for (String path : paths) {
            if (path.length() == 0) continue;
            adresse = adresse + "/" + path;
            createOrUpdateMap(map, adresse);
        }
        
        return map;
    }
    
    /**
     * METHOD: update the given map with the key and value 1
     *
     * @param map as hashmap
     * @param adresse as key
     */
    protected static void createOrUpdateMap(Map<String, Integer> map, String adresse){
        createOrUpdateMap(map, adresse, 1);
    }
    
    /**
     * METHOD: update the given map with the key value pair
     *
     * @param map as hashmap
     * @param adresse as key
     * @param value as value
     */
    protected static void createOrUpdateMap(Map<String, Integer> map, String adresse, Integer value){
        if (map.containsKey(adresse)){
            map.replace(adresse, map.get(adresse)+value);
        } else {
            map.put(adresse, value);
        }
    }
    
    /**
     * METHODE: get protocol, domain and file of an url
     * 
     * @param url
     * @return string arraywith top level domain and suffix
     */
    protected static String[] splitUrl(URL url){
        if (url == null) return new String[]{"", "", ""};
        return new String[]{url.getProtocol(), url.getHost(), url.getPath()};
    }
    
    /**
     * METHOD: check if given string is an url and returns it
     *
     * @param url as string
     * @return url, null if not possible
     */
    protected static URL getUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }
                
}
