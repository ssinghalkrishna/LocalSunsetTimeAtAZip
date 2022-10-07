package support;

import java.util.HashMap;

public class TestContext {

    private static HashMap<String, Object> testData = new HashMap<>();

    public static void setTestData(String key, Object value) {
        testData.put(key, value);
    }

    public static String getStringTestData(String key) {
        return (String) testData.get(key);
    }

}
