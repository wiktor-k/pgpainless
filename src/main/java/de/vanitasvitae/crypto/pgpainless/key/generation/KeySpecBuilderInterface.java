package de.vanitasvitae.crypto.pgpainless.key.generation;

import de.vanitasvitae.crypto.pgpainless.key.algorithm.AlgorithmSuite;
import de.vanitasvitae.crypto.pgpainless.key.algorithm.CompressionAlgorithm;
import de.vanitasvitae.crypto.pgpainless.key.algorithm.Feature;
import de.vanitasvitae.crypto.pgpainless.key.algorithm.HashAlgorithm;
import de.vanitasvitae.crypto.pgpainless.key.algorithm.KeyFlag;
import de.vanitasvitae.crypto.pgpainless.key.algorithm.SymmetricKeyAlgorithm;
import de.vanitasvitae.crypto.pgpainless.key.generation.type.KeyType;

public interface KeySpecBuilderInterface {

    WithKeyFlags ofType(KeyType type);

    interface WithKeyFlags {

        WithDetailedConfiguration withKeyFlags(KeyFlag... flags);

        WithDetailedConfiguration withDefaultKeyFlags();
    }

    interface WithDetailedConfiguration {

        WithPreferredSymmetricAlgorithms withDetailedConfiguration();

        KeySpec withStandardConfiguration();
    }

    interface WithPreferredSymmetricAlgorithms {

        WithPreferredHashAlgorithms withPreferredSymmetricAlgorithms(SymmetricKeyAlgorithm... algorithms);

        WithPreferredHashAlgorithms withDefaultSymmetricAlgorithms();

        WithFeatures withDefaultAlgorithms();

    }

    interface WithPreferredHashAlgorithms {

        WithPreferredCompressionAlgorithms withPreferredHashAlgorithms(HashAlgorithm... algorithms);

        WithPreferredCompressionAlgorithms withDefaultHashAlgorithms();

    }

    interface WithPreferredCompressionAlgorithms {

        WithFeatures withPreferredCompressionAlgorithms(CompressionAlgorithm... algorithms);

        WithFeatures withDefaultCompressionAlgorithms();

    }

    interface WithFeatures {

        WithFeatures withFeature(Feature feature);

        KeySpec done();
    }

}