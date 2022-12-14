package me.saransh13.controller;

import lombok.extern.log4j.Log4j2;
import me.saransh13.service.DemoEdmProvider;
import me.saransh13.service.DemoEntityCollectionProcessor;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
@RequestMapping(ODataController.URI)
public class ODataController {

    protected static final String URI = "/OData/V1.0";

    @Autowired
    CsdlEdmProvider edmProvider;

    @Autowired
    EntityCollectionProcessor processor;

    @RequestMapping(value = "*")
    public void process(HttpServletRequest request, HttpServletResponse response) {
        OData odata = OData.newInstance();
        ServiceMetadata edm = odata.createServiceMetadata(edmProvider,
                new ArrayList<>());
        ODataHttpHandler handler = odata.createHandler(edm);
        handler.register(processor);
        handler.process(new HttpServletRequestWrapper(request) {
            // Spring MVC matches the whole path as the servlet path
            // Olingo wants just the prefix, ie upto /OData/V1.0, so that it
            // can parse the rest of it as an OData path. So we need to override
            // getServletPath()
            @Override
            public String getServletPath() {
                return ODataController.URI;
            }
        }, response);
    }
}