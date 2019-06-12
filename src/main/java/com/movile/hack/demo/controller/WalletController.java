package com.movile.hack.demo.controller;


import com.movile.hack.demo.model.*;
import com.movile.hack.demo.repository.WalletRepository;
import com.movile.hack.demo.repository.ZoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.management.Query;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ZoopRepository zoopRepository;

    Wallet wallet = new Wallet();
    ZoopDataGeneral general = new ZoopDataGeneral();

    @PostMapping(value = "/createWallet")
    public String createWallet(@RequestBody Wallet wallet){
        Wallet wal = new Wallet();
        ZoopDataReq zoop = new ZoopDataReq();
        RestTemplate restTemplate = new RestTemplate();
        ZoopTokenCard tokenCardReq = new ZoopTokenCard();
        ZoopBuyerWithToken buyerWithToken = new ZoopBuyerWithToken();

        wal.setExpirationMonth(wallet.getExpirationMonth());
        wal.setExpirationYear(wallet.getExpirationYear());
        wal.setHolder(wallet.getHolder());
        wal.setCardNumber(wallet.getCardNumber());
        wal.setSecurityNumber(wallet.getSecurityNumber());
        wal.setFirstName(wallet.getFirstName());
        wal.setLastName(wallet.getLastName());

        zoop.setFirstName(wallet.getFirstName());
        zoop.setLastName(wallet.getLastName());

        tokenCardReq.setCard_number(wallet.getCardNumber());
        tokenCardReq.setExpiration_month(wallet.getExpirationMonth());
        tokenCardReq.setExpiration_year(wallet.getExpirationYear());
        tokenCardReq.setHolder_name(wallet.getHolder());
        tokenCardReq.setSecurity_code(wallet.getSecurityNumber());

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic enBrX3Rlc3Rfb2dtaTNUSm5WMzNVRGxqZE40bjhhUml0Og==");
        headers.add("Content-Type", "application/json");
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpEntity<ZoopDataReq> request = new HttpEntity<ZoopDataReq>(zoop, headers);
        HttpEntity<ZoopTokenCard> request1 = new HttpEntity<ZoopTokenCard>(tokenCardReq, headers);


        ZoopDataBuyer buyer = restTemplate.postForObject(
                "https://api.zoop.ws/v1/marketplaces/3249465a7753536b62545a6a684b0000/buyers",
                request,
                ZoopDataBuyer.class);

        ZoopTokenCard tokenCard = restTemplate.postForObject(
                "https://api.zoop.ws/v1/marketplaces/3249465a7753536b62545a6a684b0000/cards/tokens",
                request1,
                ZoopTokenCard.class);

        buyerWithToken.setCustomer(buyer.getId());
        buyerWithToken.setToken(tokenCard.getId());

        HttpEntity<ZoopBuyerWithToken> request2 = new HttpEntity<ZoopBuyerWithToken>(buyerWithToken, headers);
        ZoopBuyerWithToken tokenCardCostumer = restTemplate.postForObject(
                "https://api.zoop.ws/v1/marketplaces/3249465a7753536b62545a6a684b0000/cards",
                request2,
                ZoopBuyerWithToken.class);



        general.setBuyer(buyer.getId());
        general.setTokenCard(tokenCard.getId());
        general.setBuyerCard(tokenCardCostumer.getId());
        general.setClientCpf(wallet.getClientCpf());

        wal.setAmount("0.00");
        wal.setClientCpf(wallet.getClientCpf());
        wal.setId(wallet.getId());
        walletRepository.save(wal);
        zoopRepository.save(general);
        return "Wallet created and zoop features too!";
    }


    @PostMapping(value = "/pay")
    public String payment(@RequestBody Payment pay){
        Payment p = new Payment();
        RestTemplate restTemplate = new RestTemplate();
        Consumables c = new Consumables();



        List<ZoopDataGeneral> x = zoopRepository.findAll();

        p.setAmount(pay.getAmount());
        p.setCurrency(pay.getCurrency());
        System.out.println(zoopRepository.findBuyer(pay.getClientCpf()));
        p.setCustomer(x.get(1).getBuyer());
        p.setPayment_type(pay.getPayment_type());
        p.setDescription(pay.getDescription());
        p.setOn_behalf_of("05f44463d78f449bbf526c6ca5e7ff7f");
        p.setCod(generateCode(pay.getCod()));



        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic enBrX3Rlc3Rfb2dtaTNUSm5WMzNVRGxqZE40bjhhUml0Og==");
        headers.add("Content-Type", "application/json");
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpEntity<Payment> request = new HttpEntity<Payment>(p, headers);

        Payment payz = restTemplate.postForObject(
                "https://api.zoop.ws/v1/marketplaces/3249465a7753536b62545a6a684b0000/transactions",
                request,
                Payment.class);

//        if(payz.getStatus().equalsIgnoreCase("succeeded")){
//
//            c.setCod(p.getCod());
//            c.setMessage("A compra de codigo " + c.getCod() + " foi confirmada");
//            c.setTo("5511962345365");
//            c.setCarrier("TIM");
//
//            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//
//
//            restTemplate.postForObject(
//                    "http://localhost:9191/wavy/validateSms",
//                    c,
//                    Consumables.class);
//        }

        return "compra realizada, consulte com o garçom a validacao";
    }

    public String generateCode(String code){
        int cd = Integer.parseInt(code);

        cd = cd + 159;

        return String.valueOf(cd);
    }


}
