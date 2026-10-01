package com.campusshield.campusshield.soap;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.entity.OfficerAssignment;
import com.campusshield.campusshield.service.IncidentSoapService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import java.util.List;

@Endpoint
public class GetIncidentByIdEndpoint {

    private static final String NAMESPACE =
            "http://campusshield.com/soap";

    private final IncidentSoapService incidentSoapService;

    public GetIncidentByIdEndpoint(
            IncidentSoapService incidentSoapService) {

        this.incidentSoapService = incidentSoapService;
    }

    // =========================================================
    // REPORT INCIDENT
    // =========================================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "reportIncidentRequest"
    )
    @ResponsePayload
    public DOMSource reportIncident(
            @RequestPayload Element request) throws Exception {

        long userId =
                Long.parseLong(
                        getText(request, "userId")
                );

        String category =
                getText(request, "category");

        String description =
                getText(request, "description");

        String location =
                getText(request, "location");

        String severity =
                getText(request, "severity");

        Incident incident =
                incidentSoapService.reportIncident(
                        userId,
                        category,
                        description,
                        location,
                        severity
                );

        Document document = createDocument();

        Element response =
                document.createElementNS(
                        NAMESPACE,
                        "reportIncidentResponse"
                );

        document.appendChild(response);

        boolean success =
                incident != null;

        appendElement(
                document,
                response,
                "success",
                String.valueOf(success)
        );

        appendElement(
                document,
                response,
                "message",
                success
                        ? "Incident reported successfully"
                        : "Unable to report incident"
        );

        appendElement(
                document,
                response,
                "incidentId",
                success
                        ? String.valueOf(
                                incident.getIncidentId()
                        )
                        : "0"
        );

        appendElement(
                document,
                response,
                "status",
                success
                        ? incident.getStatus()
                        : "NOT_REPORTED"
        );

        return new DOMSource(document);
    }


    // =========================================================
    // GET INCIDENT BY ID
    // =========================================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "getIncidentByIdRequest"
    )
    @ResponsePayload
    public DOMSource getIncidentById(
            @RequestPayload Element request) throws Exception {

        long incidentId =
                Long.parseLong(
                        getText(request, "incidentId")
                );

        Incident incident =
                incidentSoapService.getIncidentById(incidentId);

        Document document = createDocument();

        Element response =
                document.createElementNS(
                        NAMESPACE,
                        "getIncidentByIdResponse"
                );

        document.appendChild(response);

        if (incident != null) {

            appendElement(
                    document,
                    response,
                    "incidentId",
                    String.valueOf(
                            incident.getIncidentId()
                    )
            );

            appendElement(
                    document,
                    response,
                    "category",
                    safe(incident.getCategory())
            );

            appendElement(
                    document,
                    response,
                    "description",
                    safe(incident.getDescription())
            );

            appendElement(
                    document,
                    response,
                    "location",
                    safe(incident.getLocation())
            );

            appendElement(
                    document,
                    response,
                    "severity",
                    safe(incident.getSeverity())
            );

            appendElement(
                    document,
                    response,
                    "status",
                    safe(incident.getStatus())
            );

            appendElement(
                    document,
                    response,
                    "reportedAt",
                    incident.getCreatedAt() != null
                            ? incident.getCreatedAt().toString()
                            : ""
            );
        }

        return new DOMSource(document);
    }


    // =========================================================
    // GET STUDENT INCIDENTS
    // =========================================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "getStudentIncidentsRequest"
    )
    @ResponsePayload
    public DOMSource getStudentIncidents(
            @RequestPayload Element request) throws Exception {

        long userId =
                Long.parseLong(
                        getText(request, "userId")
                );

        List<Incident> incidents =
                incidentSoapService.getStudentIncidents(userId);

        Document document = createDocument();

        Element response =
                document.createElementNS(
                        NAMESPACE,
                        "getStudentIncidentsResponse"
                );

        document.appendChild(response);

        for (Incident incident : incidents) {

            Element incidentElement =
                    document.createElementNS(
                            NAMESPACE,
                            "incidents"
                    );

            response.appendChild(incidentElement);

            appendElement(
                    document,
                    incidentElement,
                    "incidentId",
                    String.valueOf(
                            incident.getIncidentId()
                    )
            );

            appendElement(
                    document,
                    incidentElement,
                    "category",
                    safe(incident.getCategory())
            );

            appendElement(
                    document,
                    incidentElement,
                    "description",
                    safe(incident.getDescription())
            );

            appendElement(
                    document,
                    incidentElement,
                    "location",
                    safe(incident.getLocation())
            );

            appendElement(
                    document,
                    incidentElement,
                    "severity",
                    safe(incident.getSeverity())
            );

            appendElement(
                    document,
                    incidentElement,
                    "status",
                    safe(incident.getStatus())
            );

            appendElement(
                    document,
                    incidentElement,
                    "reportedAt",
                    incident.getCreatedAt() != null
                            ? incident.getCreatedAt().toString()
                            : ""
            );
        }

        return new DOMSource(document);
    }


    // =========================================================
    // UPDATE INCIDENT STATUS
    // =========================================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "updateIncidentStatusRequest"
    )
    @ResponsePayload
    public DOMSource updateIncidentStatus(
            @RequestPayload Element request) throws Exception {

        long incidentId =
                Long.parseLong(
                        getText(request, "incidentId")
                );

        String status =
                getText(request, "status");

        Incident updatedIncident =
                incidentSoapService.updateIncidentStatus(
                        incidentId,
                        status
                );

        Document document = createDocument();

        Element response =
                document.createElementNS(
                        NAMESPACE,
                        "updateIncidentStatusResponse"
                );

        document.appendChild(response);

        boolean success =
                updatedIncident != null;

        appendElement(
                document,
                response,
                "success",
                String.valueOf(success)
        );

        appendElement(
                document,
                response,
                "message",
                success
                        ? "Incident status updated successfully"
                        : "Invalid status transition or incident not found"
        );

        appendElement(
                document,
                response,
                "incidentId",
                String.valueOf(incidentId)
        );

        appendElement(
                document,
                response,
                "status",
                success
                        ? updatedIncident.getStatus()
                        : "NOT_UPDATED"
        );

        return new DOMSource(document);
    }


    // =========================================================
    // ASSIGN OFFICER
    // =========================================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "assignOfficerRequest"
    )
    @ResponsePayload
    public DOMSource assignOfficer(
            @RequestPayload Element request) throws Exception {

        long incidentId =
                Long.parseLong(
                        getText(request, "incidentId")
                );

        long officerId =
                Long.parseLong(
                        getText(request, "officerId")
                );

        OfficerAssignment assignment =
                incidentSoapService.assignOfficer(
                        incidentId,
                        officerId
                );

        Document document = createDocument();

        Element response =
                document.createElementNS(
                        NAMESPACE,
                        "assignOfficerResponse"
                );

        document.appendChild(response);

        boolean success =
                assignment != null;

        appendElement(
                document,
                response,
                "success",
                String.valueOf(success)
        );

        appendElement(
                document,
                response,
                "message",
                success
                        ? "Officer assigned successfully"
                        : "Officer assignment failed"
        );

        appendElement(
                document,
                response,
                "incidentId",
                String.valueOf(incidentId)
        );

        appendElement(
                document,
                response,
                "officerId",
                String.valueOf(officerId)
        );

        appendElement(
                document,
                response,
                "status",
                success
                        ? assignment
                                .getIncident()
                                .getStatus()
                        : "NOT_ASSIGNED"
        );

        return new DOMSource(document);
    }


    // =========================================================
    // GET INCIDENT STATISTICS
    // =========================================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "getIncidentStatisticsRequest"
    )
    @ResponsePayload
    public DOMSource getIncidentStatistics(
            @RequestPayload Element request) throws Exception {

        Document document = createDocument();

        Element response =
                document.createElementNS(
                        NAMESPACE,
                        "getIncidentStatisticsResponse"
                );

        document.appendChild(response);

        appendElement(
                document,
                response,
                "totalIncidents",
                String.valueOf(
                        incidentSoapService
                                .getTotalIncidents()
                )
        );

        appendElement(
                document,
                response,
                "reported",
                String.valueOf(
                        incidentSoapService
                                .getReportedIncidents()
                )
        );

        appendElement(
                document,
                response,
                "assigned",
                String.valueOf(
                        incidentSoapService
                                .getAssignedIncidents()
                )
        );

        appendElement(
                document,
                response,
                "inProgress",
                String.valueOf(
                        incidentSoapService
                                .getInProgressIncidents()
                )
        );

        appendElement(
                document,
                response,
                "resolved",
                String.valueOf(
                        incidentSoapService
                                .getResolvedIncidents()
                )
        );

        appendElement(
                document,
                response,
                "closed",
                String.valueOf(
                        incidentSoapService
                                .getClosedIncidents()
                )
        );

        appendElement(
                document,
                response,
                "highSeverity",
                String.valueOf(
                        incidentSoapService
                                .getHighSeverityIncidents()
                )
        );

        appendElement(
                document,
                response,
                "criticalSeverity",
                String.valueOf(
                        incidentSoapService
                                .getCriticalSeverityIncidents()
                )
        );

        return new DOMSource(document);
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Document createDocument()
            throws Exception {

        return DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument();
    }

    private void appendElement(
            Document document,
            Element parent,
            String name,
            String value) {

        Element element =
                document.createElementNS(
                        NAMESPACE,
                        name
                );

        element.setTextContent(
                value != null ? value : ""
        );

        parent.appendChild(element);
    }

    private String getText(
            Element request,
            String tagName) {

        return request
                .getElementsByTagNameNS(
                        NAMESPACE,
                        tagName
                )
                .item(0)
                .getTextContent();
    }

    private String safe(String value) {

        return value != null ? value : "";
    }
}