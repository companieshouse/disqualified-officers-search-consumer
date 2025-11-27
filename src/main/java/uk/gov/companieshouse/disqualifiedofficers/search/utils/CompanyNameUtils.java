package uk.gov.companieshouse.disqualifiedofficers.search.utils;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.disqualifiedofficers.search.model.CompanyName;

import java.util.regex.Pattern;

@Component
public class CompanyNameUtils {

    public CompanyName splitCompanyName(String name) {
        String cname = name;
        String end = "";
        for (String ending : nameEndings) {
            Pattern pattern = Pattern.compile("\\s" + ending + "$", Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(name).find() && ending.length() > end.length()) {
                cname = name.substring(0, name.length() - ending.length() - 1);
                end = name.substring(name.length() - ending.length());
            }
        }
        return new CompanyName(cname, end);
    }

    // Taken from CH-CompanyNameEndings
    private static final String[] nameEndings = {
            "AEIE",
            "ANGHYFYNGEDIG",
            "C.B.C",
            "C.C.C",
            "C.I.C",
            "CBC",
            "CBCN",
            "CBP",
            "CCC",
            "CCG CYF",
            "CCG CYFYNGEDIG",
            "CIC",
            "COMMUNITY INTEREST COMPANY",
            "COMMUNITY INTEREST P.L.C",
            "COMMUNITY INTEREST PLC",
            "COMMUNITY INTEREST PUBLIC LIMITED COMPANY",
            "CWMNI BUDDIANT C.C.C",
            "CWMNI BUDDIANT CCC",
            "CWMNI BUDDIANT CYMUNEDOL C.C.C",
            "CWMNI BUDDIANT CYMUNEDOL CCC",
            "CWMNI BUDDIANT CYMUNEDOL CYHOEDDUS CYFYNGEDIG",
            "CWMNI BUDDIANT CYMUNEDOL",
            "CWMNI BUDDSODDIA CHYFALAF NEWIDIOL",
            "CWMNI BUDDSODDIANT PENAGORED",
            "CWMNI CELL GWARCHODEDIG",
            "CWMNI CYFYNGEDIG CYHOEDDUS",
            "CYF",
            "CYFYNGEDIG",
            "EEIG",
            "EESV",
            "EOFG",
            "EOOS",
            "EUROPEAN ECONOMIC INTEREST GROUPING",
            "GEIE",
            "GELE",
            "ICVC",
            "INVESTMENT COMPANY WITH VARIABLE CAPITAL",
            "L.P",
            "L.T.D",
            "LIMITED - THE",
            "LIMITED LIABILITY PARTNERSHIP",
            "LIMITED PARTNERSHIP",
            "LIMITED THE",
            "LIMITED",
            "LIMITED-THE",
            "LIMITED...THE",
            "LIMITED..THE",
            "LIMITED.THE",
            "LLP",
            "LP",
            "LTD",
            "LTD...THE",
            "LTD..THE",
            "LTD.THE",
            "OEIC",
            "OPEN-ENDED INVESTMENT COMPANY",
            "P.L.C",
            "PAC",
            "PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
            "PARTNERIAETH CYFYNGEDIG",
            "PCC LIMITED",
            "PCC LTD",
            "PCC",
            "PLC",
            "PROTECTED CELL COMPANY",
            "PUBLIC LIMITED COMPANY .THE",
            "PUBLIC LIMITED COMPANY THE",
            "PUBLIC LIMITED COMPANY",
            "PUBLIC LIMITED COMPANY.THE",
            "SE",
            "UKEIG",
            "UK SOCIETAS",
            "UNLIMITED",
            "UNLTD"
    };
}
