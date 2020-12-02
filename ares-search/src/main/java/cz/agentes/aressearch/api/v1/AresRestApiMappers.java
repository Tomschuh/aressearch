package cz.agentes.aressearch.api.v1;

import cz.agentes.aressearch.api.v1.AresResource.GetCompanyDetailsResTo;
import cz.agentes.aressearch.api.v1.AresResource.GetCompanyDetailsResTo.Address;
import cz.agentes.aressearch.api.v1.AresResource.GetCompanyDetailsResTo.Member;
import cz.agentes.aressearch.api.v1.AresResource.GetCompanyItemResTo;
import cz.agentes.aressearch.service.CompanyService;
import cz.agentes.aressearch.service.CompanyService.GetCompanyDetailsOut;
import cz.agentes.aressearch.service.CompanyService.GetCompanyItemOut;

import java.util.ArrayList;
import java.util.List;

public class AresRestApiMappers {

    public static class GetCompanyListResToMapper {

        public static List<GetCompanyItemResTo> fromGetCompanyListOut(List<GetCompanyItemOut> out) {
            List<GetCompanyItemResTo> resTo = new ArrayList<>();
            out.forEach(itemOut -> {
                GetCompanyItemResTo itemResTo = new GetCompanyItemResTo();
                itemResTo.ico = itemOut.ico;
                itemResTo.name = itemOut.name;
                itemResTo.city = itemOut.city;
                itemResTo.number = itemOut.number;
                itemResTo.street = itemOut.street;

                resTo.add(itemResTo);
            });

            return resTo;
        }
    }

    public static class GetCompanyDetailsResToMapper {

        public static GetCompanyDetailsResTo fromGetCompanyDetailsOut(GetCompanyDetailsOut out) {
            GetCompanyDetailsResTo resTo = new GetCompanyDetailsResTo();
            resTo.name = out.name;
            resTo.ico = out.ico;
            resTo.address = new Address();
            resTo.address.city = out.address.city;
            resTo.address.state = out.address.state;
            resTo.address.street = out.address.street;
            resTo.address.number = out.address.number;
            resTo.address.zip_code = out.address.zip_code;

            resTo.memberList = new ArrayList<>();
            out.memberList.forEach(member -> {
                Member memberResTo = new Member();
                memberResTo.degreeAfter = member.degreeAfter;
                memberResTo.degreeBefore = member.degreeAfter;
                memberResTo.firstName = member.firstName;
                memberResTo.lastName = member.lastName;
                memberResTo.role = member.role;

                resTo.memberList.add(memberResTo);
            });

            return resTo;
        }
    }
}
