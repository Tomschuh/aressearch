package cz.agentes.aressearch.utils;

import cz.agentes.aressearch.service.CompanyService.GetCompanyDetailsOut;
import cz.agentes.aressearch.service.CompanyService.GetCompanyDetailsOut.AddressOut;
import cz.agentes.aressearch.service.CompanyService.GetCompanyDetailsOut.MemberOut;
import cz.agentes.aressearch.service.CompanyService.ParseCompanyIn;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static cz.agentes.aressearch.utils.AresXmlTags.*;

public class StaxXMLReader {

    private static boolean parseAddress = false;
    private static boolean parseMember = false;
    private static boolean parseStatutoryAuthority = false;
    private static boolean parseBasicData = false;

    public static GetCompanyDetailsOut parseFullXML(File file) throws FileNotFoundException, XMLStreamException {
        GetCompanyDetailsOut in = new GetCompanyDetailsOut();
        MemberOut member = new MemberOut();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                // Check XML start tags
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    // Parse address
                    if (parseAddress && parseBasicData) {
                        switch (startElement.getName().getLocalPart()) {
                            case STATE:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.address.state = xmlEvent.asCharacters().getData();
                                break;
                            case STREET:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.address.street = xmlEvent.asCharacters().getData();
                                break;
                            case ZIP_CODE:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.address.zip_code = xmlEvent.asCharacters().getData();
                                break;
                            case CITY:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.address.city = xmlEvent.asCharacters().getData();
                                break;
                            case NUMBER:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.address.number = xmlEvent.asCharacters().getData();
                                break;
                            case NUMBER_POP:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.address.number = xmlEvent.asCharacters().getData();
                                break;
                        }
                        // Parse member
                    } else if (parseBasicData) {
                        switch (startElement.getName().getLocalPart()) {
                            case NAME:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.name = xmlEvent.asCharacters().getData();
                                break;
                            case ICO:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.ico = xmlEvent.asCharacters().getData();
                                break;
                            case PLACE:
                                in.address = new AddressOut();
                                parseAddress = true;
                                break;
                        }
                    } else if (parseMember) {
                        switch (startElement.getName().getLocalPart()) {
                            case FIRSTNAME:
                                xmlEvent = xmlEventReader.nextEvent();
                                member.firstName = xmlEvent.asCharacters().getData();
                                break;
                            case LASTNAME:
                                xmlEvent = xmlEventReader.nextEvent();
                                member.lastName = xmlEvent.asCharacters().getData();
                                break;
                            case ROLE:
                                xmlEvent = xmlEventReader.nextEvent();
                                member.role = xmlEvent.asCharacters().getData();
                                break;
                            case DEGREE_BEFORE:
                                xmlEvent = xmlEventReader.nextEvent();
                                member.degreeBefore = xmlEvent.asCharacters().getData();
                                break;
                            case DEGREE_AFTER:
                                xmlEvent = xmlEventReader.nextEvent();
                                member.degreeAfter = xmlEvent.asCharacters().getData();
                                break;
                        }
                    } else {
                        switch (startElement.getName().getLocalPart()) {
                            case BASIC_DATA:
                                parseBasicData = true;
                                break;
                            case STATUTORY_AUTHORITY:
                                parseStatutoryAuthority = true;
                                break;
                            case MEMBER:
                                if (parseStatutoryAuthority) {
                                    if (in.memberList == null) {
                                        in.memberList = new ArrayList<>();
                                    }
                                    parseMember = true;
                                    member = new MemberOut();
                                }
                        }
                    }
                }
                // Check end XML tags
                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals(PLACE)) {
                        parseAddress = false;
                    } else if (endElement.getName().getLocalPart().equals(STATUTORY_AUTHORITY)) {
                        parseStatutoryAuthority = false;
                    } else if (endElement.getName().getLocalPart().equals(BASIC_DATA)) {
                        parseBasicData = false;
                    }
                    if (endElement.getName().getLocalPart().equals(MEMBER) && parseStatutoryAuthority) {
                        in.memberList.add(member);
                        member = new MemberOut();
                        parseMember = false;
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }

        return in;
    }

    public static ParseCompanyIn parseXml(File file) {
        ParseCompanyIn in = new ParseCompanyIn();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                // Check XML start tags
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    // Parse address
                    if (parseAddress && parseBasicData) {
                        switch (startElement.getName().getLocalPart()) {
                            case STREET:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.street = xmlEvent.asCharacters().getData();
                                break;
                            case CITY:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.city = xmlEvent.asCharacters().getData();
                                break;
                            case NUMBER:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.number = xmlEvent.asCharacters().getData();
                                break;
                            case NUMBER_POP:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.number = xmlEvent.asCharacters().getData();
                                break;
                        }
                    } else if (parseBasicData) {
                        switch (startElement.getName().getLocalPart()) {
                            case NAME:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.name = xmlEvent.asCharacters().getData();
                                break;
                            case ICO:
                                xmlEvent = xmlEventReader.nextEvent();
                                in.ico = xmlEvent.asCharacters().getData();
                                break;
                            case PLACE:
                                parseAddress = true;
                                break;
                        }
                    } else {
                        if (BASIC_DATA.equals(startElement.getName().getLocalPart())) {
                            parseBasicData = true;
                        }
                    }
                }
                // Check end XML tags
                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals(PLACE)) {
                        parseAddress = false;
                    }
                    if (endElement.getName().getLocalPart().equals(BASIC_DATA)) {
                        parseBasicData = false;
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }

        return in;
    }
}
