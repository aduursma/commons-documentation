package nl.agility.commons.documentation;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured3.operation.preprocess.RestAssuredPreprocessors.modifyUris;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("integration-test")
public abstract class BaseDocumentation {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @LocalServerPort
    protected int serverPort;

    protected RequestSpecification documentationSpec;
    protected RestDocumentationFilter documentationFilter;

    protected List<LinkDescriptor> pageLinks = Arrays.asList(
            linkWithRel("first").optional().description("The first page of results."),
            linkWithRel("last").optional().description("The last page of results."),
            linkWithRel("next").optional().description("The next page of results."),
            linkWithRel("prev").optional().description("The previous page of results."),

            linkWithRel("search").optional().description("The link to search criteria."),
            linkWithRel("self").optional().description("The self reference."),
            linkWithRel("profile").optional().description("The generated JSON-Schema for this resource.")
    );

    protected List<FieldDescriptor> pageFields = Arrays.asList(
            fieldWithPath("page.size").description("The configured page size."),
            fieldWithPath("page.totalElements").description("The total number of elements."),
            fieldWithPath("page.totalPages").description("The total number of pages."),
            fieldWithPath("page.number").description("The current page (zero indexed).")
    );

    protected List<FieldDescriptor> error = Arrays.asList(
            fieldWithPath("timestamp").description("The timestamp the error occurred."),
            fieldWithPath("status").description("The HTTP response code."),
            fieldWithPath("error").description("The HTTP response code description."),
            fieldWithPath("message").description("An informational error message."),
            fieldWithPath("exception").description("The actual error which occurred."),
            fieldWithPath("path").description("The requested URI.")
    );

    @Before
    public void setUp() {
        RestAssured.port = serverPort;

        this.documentationFilter = document("{method-name}",
                preprocessRequest(prettyPrint(), modifyHostname(), removeRequestHeaders()),
                preprocessResponse(prettyPrint(), modifyHostname(), removeResponseHeaders())
        );

        this.documentationSpec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(this.restDocumentation))
                .addFilter(this.documentationFilter)
                .build();
    }

    public static OperationPreprocessor modifyHostname() {
        return modifyUris().scheme("https").host("vtc.vialis.nl").removePort();
    }

    public static OperationPreprocessor removeRequestHeaders() {
        return removeHeaders("Accept");
    }

    public static OperationPreprocessor removeResponseHeaders() {
        return removeHeaders("X-Application-Context");
    }

}
