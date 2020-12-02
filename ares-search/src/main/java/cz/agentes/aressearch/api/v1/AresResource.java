package cz.agentes.aressearch.api.v1;

import cz.agentes.aressearch.api.v1.AresRestApiMappers.GetCompanyDetailsResToMapper;
import cz.agentes.aressearch.api.v1.AresRestApiMappers.GetCompanyListResToMapper;
import cz.agentes.aressearch.service.CompanyService;
import cz.agentes.aressearch.service.CompanyService.GetCompanyDetailsOut;
import cz.agentes.aressearch.service.CompanyService.GetCompanyItemOut;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ares-search")
@RequiredArgsConstructor
public class AresResource {

    private final @NonNull CompanyService companyService;

    @GetMapping
    public ResponseEntity<?> getSearchSuggestions(@Valid @RequestParam @Size(min = 3) String searchExpression,
                                                  @Valid @RequestParam(required = false) Integer numberOfSearchSuggestion) {
        // Set default value of number search suggestion
        if (numberOfSearchSuggestion == null) numberOfSearchSuggestion = 50;
        // Call service layer
        List<GetCompanyItemOut> out = companyService.getSearchSuggestions(searchExpression, numberOfSearchSuggestion);

        List<GetCompanyItemResTo> resTo = GetCompanyListResToMapper.fromGetCompanyListOut(out);

        return ResponseEntity.ok(resTo);
    }

    @GetMapping("/{ico}")
    public ResponseEntity<?> getCompanyDetails(@Valid @PathVariable String ico) throws FileNotFoundException, XMLStreamException {
        // Call service layer
        GetCompanyDetailsOut out = companyService.getCompanyDetails(ico);

        GetCompanyDetailsResTo resTo = GetCompanyDetailsResToMapper.fromGetCompanyDetailsOut(out);

        return ResponseEntity.ok(resTo);
    }

    @GetMapping("/update")
    public ResponseEntity<?> getCompanyDetails() {
        // Call service layer
        companyService.updateCompanies();

        return ResponseEntity.ok(null);
    }

    public static class GetCompanyItemResTo {
        public String name;
        public String ico;
        public String street;
        public String city;
        public String number;
    }

    public static class GetCompanyDetailsResTo {
        public String name;
        public String ico;
        public Address address;
        public List<Member> memberList;

        public static class Address {
            public String state;
            public String zip_code;
            public String city;
            public String street;
            public String number;
        }

        public static class Member {
            public String firstName;
            public String lastName;
            public String degreeBefore;
            public String degreeAfter;
            public String role;
        }
    }
}
