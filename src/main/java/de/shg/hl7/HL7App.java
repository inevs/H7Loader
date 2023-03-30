package de.shg.hl7;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.ID;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.parser.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HL7App {

    HapiContext context = new DefaultHapiContext();
    Parser parser = context.getPipeParser();

    public void start() {
        try {
            Path path = Path.of("src/main/resources/multiple868");
            String hl7String = Files.readString(path, StandardCharsets.ISO_8859_1);
            Message message = parser.parse(hl7String);

            System.out.println("Version: " + message.getVersion());
            System.out.println(message.getName());
            ;
            System.out.println(message.getMessage());
            ;

            if (message instanceof ORU_R01) {
                parseOruMessage((ORU_R01) message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }

    private void parseOruMessage(ORU_R01 oru) {
        try {
            System.out.println("Patient Results: " + oru.getPATIENT_RESULTReps());
            List<ORU_R01_PATIENT_RESULT> patientResults = oru.getPATIENT_RESULTAll();
            for (ORU_R01_PATIENT_RESULT patientResult : patientResults) {
                System.out.println("Order Observations: " + patientResult.getORDER_OBSERVATIONReps());
                int orderObservationCount = 1;
                List<ORU_R01_ORDER_OBSERVATION> orderObservations = patientResult.getORDER_OBSERVATIONAll();
                for (ORU_R01_ORDER_OBSERVATION orderObservation : orderObservations) {
                    System.out.println("OrderObservation " + orderObservationCount++);
                    System.out.println("Observations: " + orderObservation.getOBSERVATIONReps());
                    int observationCount = 1;
                    List<ORU_R01_OBSERVATION> observations = orderObservation.getOBSERVATIONAll();
                    for (ORU_R01_OBSERVATION observation : observations) {
                        System.out.println("Observation " + observationCount++);
                        OBX obx = observation.getOBX();
                        ID status = obx.getObservationResultStatus();
                        System.out.println("Observation Result No" + obx.getSetIDOBX());
                        System.out.println("Result status = " + status);
                        System.out.println(obx.getMessage());
                    }
                }
            }

        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
}
