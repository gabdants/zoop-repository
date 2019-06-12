package com.movile.hack.demo.controller;

import com.movile.hack.demo.model.Consumables;
import com.movile.hack.demo.model.Wallet;
import com.movile.hack.demo.model.ZoopDataBuyer;
import com.movile.hack.demo.model.ZoopDataReq;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
@RequestMapping("/wavy")
public class WavyController {

    Consumables cons = new Consumables();

    @PostMapping(value = "/validateSms")
    public String sendSms(@RequestBody Consumables consumables){
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Access-key", "ak-0987698");
        headers.add("Content-Type", "application/json");
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        cons.setTo(consumables.getTo());
        cons.setCarrier(consumables.getCarrier());
        cons.setMessage("A compra de codigo" + consumables.getCod() + "foi confirmada");

        HttpEntity<Consumables> request = new HttpEntity<Consumables>(cons, headers);

        Consumables resp = restTemplate.postForObject(
                "http://messaging-api.wavy.global:8080/v1/sms/send",
                request,
                Consumables.class);




        return "mesagem enviada";
    }

}
