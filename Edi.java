import org.smooks.Smooks;
import org.smooks.payload.JavaResult;
import org.smooks.payload.StringResult;
import org.smooks.edi.EDIReader;
import org.smooks.edi.EDIStreamReader;
import org.smooks.edi.EDIStreamWriter;

import java.io.FileInputStream;
import java.io.InputStream;

public class EDIValidator {
    public static void main(String[] args) {
        String configPath = "path/to/smooks-config.xml";
        String ediFilePath = "path/to/edi-file.edi";

        try {
            // Initialize Smooks with configuration
            Smooks smooks = new Smooks(configPath);

            // Read the EDI file
            InputStream ediStream = new FileInputStream(ediFilePath);
            EDIStreamReader ediReader = new EDIStreamReader(ediStream);

            // Create a result object to capture validation output
            StringResult result = new StringResult();

            // Perform validation
            smooks.filterSource(ediReader, result);

            // Output the validation results
            System.out.println("Validation Output:");
            System.out.println(result.getResult());

            // Close streams
            ediStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
