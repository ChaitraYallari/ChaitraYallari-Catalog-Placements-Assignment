import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

public class SecretSharing {

    // Method to decode the value from a given base
    private static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);
    }

    // Method to calculate the constant term using Lagrange interpolation
    private static BigInteger lagrangeInterpolation(List<BigInteger[]> points) {
        BigInteger c = BigInteger.ZERO; // Use BigInteger for constant term
        int n = points.size();

        for (int i = 0; i < n; i++) {
            BigInteger xi = points.get(i)[0];
            BigInteger yi = points.get(i)[1];
            BigInteger li = BigInteger.ONE;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    BigInteger xj = points.get(j)[0];
                    li = li.multiply(BigInteger.ZERO.subtract(xj)).divide(xi.subtract(xj));
                }
            }
            c = c.add(li.multiply(yi));
        }
        return c; // Return the constant term c
    }

    private static void processTestCase(String filePath) {
        String content;

        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject json = new JSONObject(content);

            System.out.println("JSON Content from " + filePath + ": " + json.toString(4)); // Print the JSON content

            int n = json.getJSONObject("keys").getInt("n");
            int k = json.getJSONObject("keys").getInt("k");

            List<BigInteger[]> points = new ArrayList<>(); // Change to BigInteger[]

            for (int i = 1; i <= n; i++) {
                if (json.has(String.valueOf(i))) {
                    JSONObject root = json.getJSONObject(String.valueOf(i));
                    int base = root.getInt("base");
                    String value = root.getString("value");

                    // Decode the y value
                    BigInteger yDecoded = decodeValue(value, base);
                    points.add(new BigInteger[]{BigInteger.valueOf(i), yDecoded}); // Store x as BigInteger
                } else {
                    System.err.println("Key " + i + " not found in JSON.");
                }
            }

            // Calculate the constant term c using Lagrange interpolation
            BigInteger constantTerm = lagrangeInterpolation(points);
            System.out.println("The constant term (c) of the polynomial in " + filePath + " is: " + constantTerm);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            System.err.println("JSON Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Test case file paths
        String[] testFiles = {"data1.json", "data2.json"};

        // Process each test case
        for (String testFile : testFiles) {
            processTestCase(testFile);
        }
    }
}
