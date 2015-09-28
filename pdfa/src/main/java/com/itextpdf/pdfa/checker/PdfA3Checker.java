package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.Arrays;
import java.util.HashSet;

public class PdfA3Checker extends PdfA2Checker{
    protected static final HashSet<PdfName> allowedAFRelationships = new HashSet<PdfName>(Arrays.asList(
            PdfName.Source, PdfName.Data, PdfName.Alternative,
            PdfName.Supplement, PdfName.Unspecified));

    public PdfA3Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    @Override
    protected void checkFileSpec(PdfDictionary fileSpec) {
        PdfName relationship = fileSpec.getAsName(PdfName.AFRelationship);
        if (relationship == null || !allowedAFRelationships.contains(relationship)) {
            throw new PdfAConformanceException(PdfAConformanceException.FileSpecificationDictionaryShallContainOneOfThePredefinedAFRelationshipKeys);
        }

        if (fileSpec.containsKey(PdfName.EF)) {
            if (!fileSpec.containsKey(PdfName.F) || !fileSpec.containsKey(PdfName.UF) || !fileSpec.containsKey(PdfName.Desc)) {
                throw new PdfAConformanceException(PdfAConformanceException.FileSpecificationDictionaryShallContainFKeyUFKeyAndDescKey);
            }

            PdfDictionary ef = fileSpec.getAsDictionary(PdfName.EF);
            PdfStream embeddedFile = ef.getAsStream(PdfName.F);
            if (embeddedFile == null) {
                throw new PdfAConformanceException(PdfAConformanceException.EFKeyOfFileSpecificationDictionaryShallContainDictionaryWithValidFKey);
            }

            PdfDictionary params = embeddedFile.getAsDictionary(PdfName.Params);
            if (params == null) {
                throw new PdfAConformanceException(PdfAConformanceException.EmbeddedFileShallContainParamsKeyWithDictionaryAsValue);
            }

            if (params.getAsString(PdfName.ModDate) == null) {
                throw new PdfAConformanceException(PdfAConformanceException.EmbeddedFileShallContainParamsKeyWithValidModdateKey);
            }
        }
    }
}
