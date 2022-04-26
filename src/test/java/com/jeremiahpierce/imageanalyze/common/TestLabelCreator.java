package com.jeremiahpierce.imageanalyze.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestLabelCreator {

    @Test
    void testLabelCreatorWithAbsolutePath() {

        String created = LabelCreator.createNewLabel("c:\\users\\omepath\\..\\that\\might\\malicious.jpg");
        assertEquals("malicious.jpg", created);

    }
    
}
