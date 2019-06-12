package com.movile.hack.demo.repository;

import com.movile.hack.demo.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalletRepository extends MongoRepository<Wallet, String> {

    Wallet findByClientCpf(String cpf);


}
