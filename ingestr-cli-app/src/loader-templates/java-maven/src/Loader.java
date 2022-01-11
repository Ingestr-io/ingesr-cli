
import io.ingestr.loaderframework.builders.BuilderFunctions;
import io.ingestr.loaderframework.entities.DataType;
import io.ingestr.loaderframework.entities.DeregistrationMethod;
import io.ingestr.loaderframework.entities.FieldType;
import io.ingestr.loaderframework.entities.Ingestion;
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
