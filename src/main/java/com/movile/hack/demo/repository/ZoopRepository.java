package com.movile.hack.demo.repository;

import com.movile.hack.demo.model.Wallet;
import com.movile.hack.demo.model.ZoopDataGeneral;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ZoopRepository extends MongoRepository<ZoopDataGeneral, String> {

    @Query("{'clientCpf' : { 'clientCpf' : ?0 }}")
    ZoopDataGeneral findBuyer(String clientCpf);


}
