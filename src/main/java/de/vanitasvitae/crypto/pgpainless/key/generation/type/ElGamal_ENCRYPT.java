package de.vanitasvitae.crypto.pgpainless.key.generation.type;

import de.vanitasvitae.crypto.pgpainless.algorithm.PublicKeyAlgorithm;
import de.vanitasvitae.crypto.pgpainless.key.generation.type.length.ElGamalLength;

public class ElGamal_ENCRYPT extends ElGamal_GENERAL {

    ElGamal_ENCRYPT(ElGamalLength length) {
        super(length);
    }

    @Override
    public PublicKeyAlgorithm getAlgorithm() {
        return PublicKeyAlgorithm.ELGAMAL_ENCRYPT;
    }
}
