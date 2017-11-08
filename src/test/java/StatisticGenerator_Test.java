import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.*;

/**
 * @author Danilo Morgado
 */
public class StatisticGenerator_Test {
    
    private static Long timeUsing;
    private static Long timeGeneral;
    private static String methode = "";
    private static Integer assertCount;
    
    private static File urlFile;
    private static File pos2File;
    private static File emptyFile;
    private static File numberlistFile;
    private static File numberlistFailFile;
    private static File outlierFile;
    private static String fixString;
    private static URL fixURL;
    private static String noURLstring;
    private static Integer stressElements;
    
    private static final File NULLFILE = null;
    private static final String EMPTYSTRING = "";
    private static final String NULLSTRING = null;
    private static final URL NULLURL = null;
    private static final List<String> NULLLIST = null;
    
    private static final List<String> TEST_STRING_LIST = new LinkedList<>();
    private static final List<URL> TEST_URL_LIST = new LinkedList<>();
    private static final Map<String, Integer> EXPECTED_STRING_MAP_FULL = new HashMap<>();
    private static final Map<URL, Integer> EXPECTED_URL_MAP_FULL = new HashMap<>();
    private static final Map<String, Integer> EXPECTED_STRING_MAP_ONE = new HashMap<>();
    private static final Map<URL, Integer> EXPECTED_URL_MAP_ONE = new HashMap<>();
    private static final List<String> STRESS_LIST = new LinkedList<>();
    private static final List<String> EXPECTED_NUMBERLIST = new LinkedList<>();
        
    private static LinkedList<String> generateTestList(){
        LinkedList<String> list = new LinkedList();
        
        list.add("http://lips.informatik.uni-leipzig.de/");
        list.add("http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Rahm_Erhard");
        list.add("http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Middendorf");
        list.add("http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Merkle");
        list.add("http://lips.informatik.uni-leipzig.de/browse");
        list.add("http://lips.informatik.uni-leipzig.de/browse/results");
        list.add("http://lips.informatik.uni-leipzig.de/browse/results?page=1");
        list.add("http://lips.informatik.uni-leipzig.de/browse/results?page=2");
        list.add("http://lips.informatik.uni-leipzig.de/browse/results?page=3");
        list.add("http://lips.informatik.uni-leipzig.de/files/m_kaehler_master_thesis.pdf");
        list.add("http://lips.informatik.uni-leipzig.de/files/ba.pdf");
        list.add("http://lips.informatik.uni-leipzig.de/files/marktgleichgewichte_bei_risikobewertung_durch_individuelle_abweichungsmasse_diplomarbeit.pdf");
        
        return list;
    }
    
    private static Map<String, Integer> generateExpectedMap(){
        Map<String, Integer> map = new HashMap<>();
        
        map.put("http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Middendorf", 1);
        map.put("http://lips.informatik.uni-leipzig.de", 12);
        map.put("http://lips.informatik.uni-leipzig.de/browse/results/field_authors", 3);
        map.put("http://lips.informatik.uni-leipzig.de/files/marktgleichgewichte_bei_risikobewertung_durch_individuelle_abweichungsmasse_diplomarbeit.pdf", 1);
        map.put("http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Rahm_Erhard", 1);
        map.put("http://lips.informatik.uni-leipzig.de/files/ba.pdf", 1);
        map.put("http://lips.informatik.uni-leipzig.de/browse", 8);
        map.put("http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Merkle", 1);
        map.put("http://lips.informatik.uni-leipzig.de/browse/results", 7);
        map.put("http://lips.informatik.uni-leipzig.de/files", 3);
        map.put("http://lips.informatik.uni-leipzig.de/files/m_kaehler_master_thesis.pdf", 1);
        
        return map;
    }
    
    private static List<String> generateExpectedNumberList(){
        List<String> list = new LinkedList();
        
        list.add("1 http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Middendorf http://lips.informatik.uni-leipzig.de/files/marktgleichgewichte_bei_risikobewertung_durch_individuelle_abweichungsmasse_diplomarbeit.pdf http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Rahm_Erhard http://lips.informatik.uni-leipzig.de/files/ba.pdf http://lips.informatik.uni-leipzig.de/browse/results/field_authors/Merkle http://lips.informatik.uni-leipzig.de/files/m_kaehler_master_thesis.pdf");
        list.add("3 http://lips.informatik.uni-leipzig.de/browse/results/field_authors http://lips.informatik.uni-leipzig.de/files");
        list.add("7 http://lips.informatik.uni-leipzig.de/browse/results");
        list.add("8 http://lips.informatik.uni-leipzig.de/browse");
        list.add("12 http://lips.informatik.uni-leipzig.de");
        
        return list;
    }
    
    public static List<String> generateStressList(Integer stressElements){
        List<String> protocoll_list = new LinkedList();
        protocoll_list.add("http");
        protocoll_list.add("https");
        protocoll_list.add("ftp");
        
        String domain = "uni-leipzig.de";
        
        List<String> host_list = new LinkedList();
        while (host_list.size() <= 2){
            String random = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(1,5) + 5);
            if (host_list.contains(random)) continue;
            host_list.add(random);
        }
        
        List<String> path_list = new LinkedList();
        while (path_list.size() <= 10){
            String random = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(1,5) + 5);
            if (path_list.contains(random)) continue;
            path_list.add(random);
        }
        
        List<String> list = new LinkedList();
        while (list.size() < stressElements){
            List<String> tmp = new LinkedList();
            Integer protocoll = RandomUtils.nextInt(0,protocoll_list.size()-1);
            Integer host = RandomUtils.nextInt(0,host_list.size()-1);
            Integer depth = RandomUtils.nextInt(0,path_list.size()/2);
            String url = protocoll_list.get(protocoll) + "://" + host_list.get(host) + "." + domain;
            
            for (int i = 0; i < depth; i++){
                Integer path;
                do {
                    path = RandomUtils.nextInt(0,path_list.size()-1);
                }
                while(tmp.contains(path_list.get(path)));
                tmp.add(path_list.get(path));
                url = url + "/" + path_list.get(path);
            }
            list.add(url);
        }
        
        return list;
    }
    
    private static void assertTest(String order, Object obj){
        assertTest(order, obj, null);
    }
    
    private static void assertTest(String order, Object obj, Object ref){
        switch(order){
            case "that":
                assertThat(obj, is(ref));
                break;
            case "true":
                Boolean bol = (Boolean) obj;
                assertTrue(bol);
                break;
            case "null":
                assertThat(obj, is(nullValue()));
                break;
            case "notNull":
                assertThat(obj, is(not(nullValue())));
                break;
            default:
                break;
        }
        assertCount++;
    }
    
    @BeforeClass
    public static void setUpClass() {
        timeGeneral = 0l;
        
        String path = "src/main/resources/";
        
        urlFile = new File(path + "url.txt");
        pos2File = new File(path + "url_pos2.txt");
        emptyFile = new File(path + "url_empty.txt");
        numberlistFile = new File(path + "numberlist.txt");
        numberlistFailFile = new File(path + "numberlist_fail.txt");
        outlierFile = new File(path + "outlier.txt");
        
        assertTrue(urlFile.exists());
        assertTrue(pos2File.exists());
        assertTrue(emptyFile.exists());
        assertTrue(numberlistFile.exists());
        assertTrue(numberlistFailFile.exists());
        assertTrue(outlierFile.exists());
        
        for (String strg : generateTestList()){
            TEST_STRING_LIST.add(strg);
            try {
                TEST_URL_LIST.add(new URL(strg));
            } catch (MalformedURLException ex) {}
        }
        assertThat(TEST_STRING_LIST.size(), is(12));
        assertThat(TEST_URL_LIST.size(), is(12));
        
        for (Map.Entry<String, Integer> entry : generateExpectedMap().entrySet()) {
            EXPECTED_STRING_MAP_FULL.put(entry.getKey(), entry.getValue());
            try {
                EXPECTED_URL_MAP_FULL.put(new URL(entry.getKey()), entry.getValue());
            } catch (MalformedURLException ex) {}
        }
        assertThat(EXPECTED_STRING_MAP_FULL.size(), is(11));
        assertThat(EXPECTED_URL_MAP_FULL.size(), is(11));
        
        fixString = "http://www.uni-leipzig.de/index.html";
        try {
            fixURL = new URL(fixString);
        } catch (MalformedURLException ex) {}
        Boolean isURL = true;
        do {
            noURLstring = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(1,fixString.length()-5) + 5);
            try {
                URL url = new URL(noURLstring);
            } catch (MalformedURLException ex) { isURL = false; }
        } while (fixString.equals(noURLstring) || isURL);
        EXPECTED_STRING_MAP_ONE.put("http://www.uni-leipzig.de", 1);
        EXPECTED_STRING_MAP_ONE.put("http://www.uni-leipzig.de/index.html", 1);
        assertThat(EXPECTED_STRING_MAP_ONE.size(), is(2));
        try {
            EXPECTED_URL_MAP_ONE.put(new URL("http://www.uni-leipzig.de"), 1);
            EXPECTED_URL_MAP_ONE.put(new URL("http://www.uni-leipzig.de/index.html"), 1);
        } catch (MalformedURLException ex) {}
        assertThat(EXPECTED_URL_MAP_ONE.size(), is(2));
        
        for (String strg : generateExpectedNumberList()) EXPECTED_NUMBERLIST.add(strg);
        assertThat(EXPECTED_NUMBERLIST.size(), is(5));
        
        stressElements = RandomUtils.nextInt(5000,10000) + 5000;
        List<String> list = generateStressList(stressElements);
        assertThat(list.size(), is(stressElements));
        for (String s : list) STRESS_LIST.add(s);
        assertThat(STRESS_LIST.size(), is(stressElements));
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.format("=== StatisticGenerator_Test ===%n");
        System.out.format("Testtime of class: %d ms [%d us]%n", (timeGeneral / 1000000), (timeGeneral / 1000));
        System.out.format("Stresstest had %d sites.%n", stressElements);
    }
    
    @Before
    public void setUp() {
        assertCount = 0;
        timeUsing = System.nanoTime();
    }
    
    @After
    public void tearDown() {
        long time = System.nanoTime();
        timeGeneral += (time - timeUsing);
        System.out.format("Time for '%45s': % 4d ms [% 7d us] for % 6d asserts.%n", methode, ((time - timeUsing) / 1000000), ((time - timeUsing) / 1000), assertCount);
    }
    
    @Test
    public void check_stressTest(){
        methode = "check_stressTest";
        
        Map<String, Integer> map = new HashMap<>();
        assertTest("that", map.size(), 0);
        
        map = StatisticGenerator.getUrlStatisticMap(STRESS_LIST);
        assertTest("true", (map.size()>0));
        
        for (String s : STRESS_LIST){
            assertTest("true", map.containsKey(s));
            assertTest("true", (map.get(s)>=1));
        }
    }
    
    @Test
    public void check_getUrlStatisticList(){
        methode = "check_getUrlStatisticList_forFile";
        
        List<String> file_numberlist = StatisticGenerator.getUrlStatisticList(urlFile);
        assertTest("that", file_numberlist.size(), EXPECTED_NUMBERLIST.size());
        for (String s : EXPECTED_NUMBERLIST){
            assertTest("true", file_numberlist.contains(s));
        }
        
        List<String> list_numberlist = StatisticGenerator.getUrlStatisticList(TEST_STRING_LIST);
        assertTest("that", list_numberlist.size(), EXPECTED_NUMBERLIST.size());
        for (String s : EXPECTED_NUMBERLIST){
            assertTest("true", list_numberlist.contains(s));
        }
        
        List<String> fixStrg_numberlist = StatisticGenerator.getUrlStatisticList(fixString);
        assertTest("that", fixStrg_numberlist.size(), 1);
        assertTest("true", fixStrg_numberlist.contains("1 http://www.uni-leipzig.de http://www.uni-leipzig.de/index.html"));
        
        List<String> fixURL_numberlist = StatisticGenerator.getUrlStatisticList(fixURL);
        assertTest("that", fixURL_numberlist.size(), 1);
        assertTest("true", fixURL_numberlist.contains("1 http://www.uni-leipzig.de http://www.uni-leipzig.de/index.html"));
    }
    
    @Test
    public void check_getUrlStatisticMap_forFile(){
        methode = "check_getUrlStatistic_forFile";
        
        Map<String, Integer> map_url = StatisticGenerator.getUrlStatisticMap(urlFile);
        assertTest("that", map_url.size(), EXPECTED_STRING_MAP_FULL.size());
        
        for (Map.Entry<String, Integer> entry : EXPECTED_STRING_MAP_FULL.entrySet()){
            assertTest("that", entry.getValue(), map_url.get(entry.getKey()));
        }
        
        Map<String, Integer> map_empty = StatisticGenerator.getUrlStatisticMap(emptyFile);
        assertTest("that", map_empty.size(), 0);
        
        Map<String, Integer> map_pos2_noValue = StatisticGenerator.getUrlStatisticMap(pos2File);
        assertTest("that", map_pos2_noValue.size(), 0);
        
        Map<String, Integer> map_pos2_val1 = StatisticGenerator.getUrlStatisticMap(pos2File, 1);
        assertTest("that", map_pos2_val1.size(), 0);
        
        Map<String, Integer> map_pos2_val2 = StatisticGenerator.getUrlStatisticMap(pos2File, 2);
        assertTest("that", map_pos2_val2.size(), EXPECTED_STRING_MAP_FULL.size());
        
        for (Map.Entry<String, Integer> entry : EXPECTED_STRING_MAP_FULL.entrySet()){
            assertTest("that", entry.getValue(), map_pos2_val2.get(entry.getKey()));
        }
        
        Map<String, Integer> map_pos2_val3 = StatisticGenerator.getUrlStatisticMap(pos2File, 3);
        assertTest("that", map_pos2_val3.size(), 0);
        
        Map<String, Integer> map_null = StatisticGenerator.getUrlStatisticMap(NULLFILE);
        assertTest("that", map_null.size(), 0);
    }
            
    @Test
    public void check_convertBufferedReaderToList(){
        methode = "check_convertBufferedReaderToList";
        
        String empty_file = "";
        String file0 = "";
        String file1 = "";
        for (String strg : TEST_STRING_LIST){
            file0 = file0 + strg + "\n";
            file1 = file1 + "# " + strg + "\n";
        }
                
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file0.getBytes())));
        List<String> list = StatisticGenerator.convertBufferedReaderToList(br, 0);
        assertTest("that", list.size(), TEST_STRING_LIST.size());
        
        Map<String, Integer> map = StatisticGenerator.getUrlStatisticMap(list);
        assertTest("that", map.size(), EXPECTED_STRING_MAP_FULL.size());
        
        for (Map.Entry<String, Integer> entry : EXPECTED_STRING_MAP_FULL.entrySet()){
            assertTest("that", entry.getValue(), map.get(entry.getKey()));
        }
        
        list.clear();
        br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file1.getBytes())));
        list = StatisticGenerator.convertBufferedReaderToList(br, 1);
        assertTest("that", list.size(), TEST_STRING_LIST.size());
        
        map = StatisticGenerator.getUrlStatisticMap(list);
        assertTest("that", map.size(), EXPECTED_STRING_MAP_FULL.size());
        
        for (Map.Entry<String, Integer> entry : EXPECTED_STRING_MAP_FULL.entrySet()){
            assertTest("that", entry.getValue(), map.get(entry.getKey()));
        }
        
        list.clear();
        br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file1.getBytes())));
        list = StatisticGenerator.convertBufferedReaderToList(br, 2);
        assertTest("that", list.size(), 0);
        
        list.clear();
        br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(empty_file.getBytes())));
        list = StatisticGenerator.convertBufferedReaderToList(br, 0);
        assertTest("that", list.size(), 0);
    }
    
    @Test
    public void check_getNumberList(){
        methode = "check_getNumberList";
        
        Map<String, Integer> map = StatisticGenerator.getUrlStatisticMap(TEST_STRING_LIST);
        
        List<String> list = StatisticGenerator.getNumberList(map);
        
        for (String s : EXPECTED_NUMBERLIST){
            assertTest("true", list.contains(s));
        }
        
        Map<String, Integer> stress_map = StatisticGenerator.getUrlStatisticMap(STRESS_LIST);
        
        List<String> stress_list = StatisticGenerator.getNumberList(stress_map);
        assertTest("true", stress_list.size()>0);
        
        Integer last = Integer.MAX_VALUE;
        for (String s : stress_list){
            String[] split = s.split("\\s+");
            Integer i = Integer.parseInt(split[0]);
            assertTest("true", (i<=last));
            last = i;
        }
    }
    
    @Test
    public void check_getOutliers(){
        methode = "check_getOutliers";
        
        Map<String, Integer> map = StatisticGenerator.getUrlStatisticMap(TEST_STRING_LIST);
        
        List<String> list = StatisticGenerator.getNumberList(map);
        
        List<String> all = StatisticGenerator.getOutlierList(list);
        assertTest("that", all.size(), 0);
        
        List<String> nulllist = StatisticGenerator.getOutlierList(NULLLIST);
        assertTest("that", nulllist.size(), 0);
        
        List<String> numberfile = StatisticGenerator.getOutlierList(numberlistFile);
        assertTest("that", numberfile.size(), 0);
        
        List<String> outlier = StatisticGenerator.getOutlierList(outlierFile);
        assertTest("that", outlier.size(), 2);
        assertTest("true", outlier.contains("205 http://lips.informatik.uni-leipzig.de/browse15"));
        assertTest("true", outlier.contains("206 http://lips.informatik.uni-leipzig.de/browse16"));
        
        List<String> fail = StatisticGenerator.getOutlierList(numberlistFailFile);
        assertTest("that", fail.size(), 2);
        assertTest("true", fail.contains("205 http://lips.informatik.uni-leipzig.de/browse15"));
        assertTest("true", fail.contains("206 http://lips.informatik.uni-leipzig.de/browse16"));
    }
    
    @Test
    public void check_getUrlStatisticMap_forList(){
        methode = "check_getUrlStatistic_forList";
        
        Map<String, Integer> string_map = StatisticGenerator.getUrlStatisticMap(TEST_STRING_LIST);
        
        assertTest("that", string_map.size(), EXPECTED_STRING_MAP_FULL.size());

        for (Map.Entry<String, Integer> entry : EXPECTED_STRING_MAP_FULL.entrySet()){
            assertTest("that", entry.getValue(), string_map.get(entry.getKey()));
        }
     
        Map<String, Integer> url_map = StatisticGenerator.getUrlStatisticMap(TEST_URL_LIST);
        
        assertTest("that", url_map.size(), EXPECTED_STRING_MAP_FULL.size());
        
        for (Map.Entry<String, Integer> entry : EXPECTED_STRING_MAP_FULL.entrySet()){
            assertTest("that", entry.getValue(), url_map.get(entry.getKey()));
        }
    }
    
    @Test
    public void check_getUrlStatisticMap_forVariable(){
        methode = "check_getUrlStatistic_forVariable";
        
        Map<String, Integer> map_fixString = StatisticGenerator.getUrlStatisticMap(fixString);
        assertTest("that", map_fixString.size(), EXPECTED_STRING_MAP_ONE.size());
        for (Map.Entry<String, Integer> entry : EXPECTED_STRING_MAP_ONE.entrySet()){
            assertTest("that", entry.getValue(), map_fixString.get(entry.getKey()));
        }
        
        Map<String, Integer> map_fixURL = StatisticGenerator.getUrlStatisticMap(fixURL);
        assertTest("that", map_fixURL.size(), EXPECTED_URL_MAP_ONE.size());
        for (Map.Entry<URL, Integer> entry : EXPECTED_URL_MAP_ONE.entrySet()){
            assertTest("that", entry.getValue(), map_fixURL.get(entry.getKey().toString()));
        }
        
        Map<String, Integer> map_noURLstring = StatisticGenerator.getUrlStatisticMap(noURLstring);
        assertTest("that", map_noURLstring.size(), 0);

        Map<String, Integer> map_emptyString = StatisticGenerator.getUrlStatisticMap(EMPTYSTRING);
        assertTest("that", map_emptyString.size(), 0);

        Map<String, Integer> map_nullString = StatisticGenerator.getUrlStatisticMap(NULLSTRING);
        assertTest("that", map_nullString.size(), 0);
    }
    
    @Test
    public void check_createOrUpdateMap(){
        methode = "check_createOrUpdateMap";
        
        Map<String, Integer> map = new HashMap<>();
        assertTest("that", map.size(), 0);
        
        assertTest("null", map.get(fixString));
        assertTest("true", !map.containsKey(fixString));
        StatisticGenerator.createOrUpdateMap(map, fixString);
        assertTest("true", map.containsKey(fixString));
        assertTest("that", map.get(fixString), 1);
        
        Integer random = RandomUtils.nextInt(1,15);
        StatisticGenerator.createOrUpdateMap(map, fixString, random);
        assertTest("true", map.containsKey(fixString));
        assertTest("that", map.get(fixString), random + 1);
    }
    
    @Test
    public void check_mergeMaps(){
        methode = "check_mergeMaps";
        
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> merge_map = new HashMap<>();
        Map<String, Integer> null_map = null;
        
        Integer map_elements = RandomUtils.nextInt(10,15);
        Integer merge_map_elements = RandomUtils.nextInt(5,10);
        
        List<String> list = new LinkedList();
        while (list.size() < map_elements){
            String random = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(20,25) + 5);
            if (list.contains(random)) continue;
            map.put(random, list.size());
            list.add(random);
        }
        
        assertTest("that", list.size(), map_elements);
        assertTest("that", map.size(), map_elements);
        assertTest("null", null_map);
        
        while (list.size() < (map_elements + merge_map_elements)){
            String random = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(20,25) + 5);
            if (list.contains(random)) continue;
            merge_map.put(random, list.size());
            list.add(random);
        }
        
        assertTest("that", list.size(), (map_elements + merge_map_elements));
        assertTest("that", merge_map.size(), merge_map_elements);
        
        StatisticGenerator.mergeMaps(map, map);
        assertTest("that", map.size(), map_elements);
        
        StatisticGenerator.mergeMaps(merge_map, merge_map);
        assertTest("that", merge_map.size(), merge_map_elements);
        
        for (Map.Entry<String, Integer> entry : merge_map.entrySet()){
            assertTest("null", map.get(entry.getKey()));
        }
        
        for (Map.Entry<String, Integer> entry : merge_map.entrySet()){
            assertTest("true", !map.containsKey(entry.getKey()));
        }
        
        Map<String, Integer> copy = new HashMap<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            copy.put(entry.getKey(), entry.getValue());
        }
        
        assertTest("that", map.size(), copy.size());
        for (Map.Entry<String, Integer> entry : copy.entrySet()){
            assertTest("true", map.containsKey(entry.getKey()));
        }
        
        StatisticGenerator.mergeMaps(map, null_map);
        for (Map.Entry<String, Integer> entry : copy.entrySet()){
            assertTest("true", map.containsKey(entry.getKey()));
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            assertTest("true", copy.containsKey(entry.getKey()));
        }
        
        StatisticGenerator.mergeMaps(null_map, map);
        for (Map.Entry<String, Integer> entry : copy.entrySet()){
            assertTest("true", map.containsKey(entry.getKey()));
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            assertTest("true", copy.containsKey(entry.getKey()));
        }
        
        StatisticGenerator.mergeMaps(map, merge_map);
        assertTest("that", map.size(), (map_elements + merge_map_elements));
        
        for (Map.Entry<String, Integer> entry : merge_map.entrySet()){
            assertTest("notNull", map.get(entry.getKey()));
        }
        
        for (Map.Entry<String, Integer> entry : merge_map.entrySet()){
            assertTest("true", map.containsKey(entry.getKey()));
        }
        
    }
    
    @Test
    public void check_splitUrl(){
        methode = "check_splitUrl";
        
        String[] split_fixString = StatisticGenerator.splitUrl(fixURL);
        assertTest("that", split_fixString.length, 3);
        assertTest("that", split_fixString[0], "http");
        assertTest("that", split_fixString[1], "www.uni-leipzig.de");
        assertTest("that", split_fixString[2], "/index.html");
        
        String[] split_nullUrl = StatisticGenerator.splitUrl(NULLURL);
        assertTest("that", split_nullUrl.length, 3);
        for (String strg : split_nullUrl){
            assertTest("that", strg, "");
        }
    }
    
    @Test
    public void check_getUrl(){
        methode = "check_getUrl";
        
        URL url_fixString = StatisticGenerator.getUrl(fixString);
        assertTest("that", url_fixString.toString(), fixString);
        
        URL url_noURLstring = StatisticGenerator.getUrl(noURLstring);
        assertTest("null", url_noURLstring);
        
        URL url_emptystring = StatisticGenerator.getUrl(EMPTYSTRING);
        assertTest("null", url_emptystring);
        
        URL url_nullstring = StatisticGenerator.getUrl(NULLSTRING);
        assertTest("null", url_nullstring);
    }
    
}
