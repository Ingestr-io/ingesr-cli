
import io.ingestr.framework.builders.BuilderFunctions;
import io.ingestr.framework.entities.DataType;
import io.ingestr.framework.entities.DeregistrationMethod;
import io.ingestr.framework.entities.FieldType;
import io.ingestr.framework.entities.Ingestion;
import io.ingestr.loader.Ingestr;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Loader {

    public static void main(String[] args) {
        Ingestr.build(args)
                //add loader code
                .start();

    }
}
