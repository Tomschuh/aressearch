package cz.agentes.aressearch.mappers;

import cz.agentes.aressearch.domain.Company;
import cz.agentes.aressearch.service.CompanyService.GetCompanyItemOut;

import java.util.ArrayList;
import java.util.List;

public class CompanyMappers {

    public static class GetCompanyListOutMapper {

        public static List<GetCompanyItemOut> fromCompanyList(List<Company> companies) {
            List<GetCompanyItemOut> out = new ArrayList<>();
            companies.forEach(company -> {
                GetCompanyItemOut itemOut = new GetCompanyItemOut();
                itemOut.ico = company.getIco();
                itemOut.name = company.getName();
                itemOut.city = company.getCity();
                itemOut.street = company.getStreet();
                itemOut.number = company.getNumber();

                out.add(itemOut);
            });

            return out;
        }
    }
}
