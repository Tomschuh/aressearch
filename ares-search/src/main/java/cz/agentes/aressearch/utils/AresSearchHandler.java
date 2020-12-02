package cz.agentes.aressearch.utils;

import cz.agentes.aressearch.service.CompanyService.ParseCompanyIn;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AresSearchHandler extends DefaultHandler {
    private static final String ICO = "are:ICO";
    private static final String NAME = "are:ObchodniFirma";
    private static final String PLACE = "are:sidlo";
    private static final String STATE = "are:stat";
    private static final String ZIP_CODE = "are:psc";
    private static final String DISTRICT = "are:okres";
    private static final String CITY = "are:obec";
    private static final String STREET = "are:ulice";
    private static final String NUMBER = "are:cislo/Txt";
    private static final String DATE_DELETED = "are:DatumVymazu";
    private static final String TEXT = "are:Text";
    private static final String SCOPE_OF_BUSINESS = "are:PredmetPodnikani";
    private static final String MEMBER = "are:Clen";

    private ParseCompanyIn company;
    private String elementValue;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        elementValue = new String (ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        company = new ParseCompanyIn();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
//        switch (qName) {
//            case
//            case ICO:
//                company.ico = elementValue
//                break;
//            case ARTICLE:
//                website.articleList.add(new BaeldungArticle());
//        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case ICO:
                company.ico = elementValue;
                break;
            case NAME:
                company.name = elementValue;
                break;
        }
    }

    public ParseCompanyIn getCompany() {
        return company;
    }
}
