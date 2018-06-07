package de.vanitasvitae.crypto.pgpainless.decryption_verification;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.util.Iterator;
import java.util.Map;

import de.vanitasvitae.crypto.pgpainless.PainlessResult;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;

public class SignatureVerifyingInputStream extends FilterInputStream {

    private final PGPObjectFactory objectFactory;
    private final Map<Long, PGPOnePassSignature> onePassSignatures;
    private final PainlessResult.Builder resultBuilder;

    protected SignatureVerifyingInputStream(InputStream inputStream,
                                            PGPObjectFactory objectFactory,
                                            Map<Long, PGPOnePassSignature> onePassSignatures,
                                            PainlessResult.Builder resultBuilder) {
        super(inputStream);
        this.objectFactory = objectFactory;
        this.resultBuilder = resultBuilder;
        this.onePassSignatures = onePassSignatures;
    }

    private void updateOnePassSignatures(byte data) {
        for (PGPOnePassSignature signature : onePassSignatures.values()) {
            signature.update(data);
        }
    }

    private void updateOnePassSignatures(byte[] b, int off, int len) {
        for (PGPOnePassSignature signature : onePassSignatures.values()) {
            signature.update(b, off, len);
        }
    }

    private void validateOnePassSignatures() throws IOException {
        if (onePassSignatures.isEmpty()) {
            return;
        }

        try {
            PGPSignatureList signatureList = null;
            Iterator objectIterator = objectFactory.iterator();
            while (objectIterator.hasNext() && signatureList == null) {
                Object object = objectIterator.next();
                if (object instanceof PGPSignatureList) {
                    signatureList = (PGPSignatureList) object;
                }
            }

            if (signatureList == null || signatureList.isEmpty()) {
                throw new IOException("Verification failed - No Signatures found!");
            }

            for (PGPSignature signature : signatureList) {
                PGPOnePassSignature onePassSignature = onePassSignatures.get(signature.getKeyID());
                if (onePassSignature == null) {
                    continue;
                }
                if (!onePassSignature.verify(signature)) {
                    throw new SignatureException("Bad Signature of key " + signature.getKeyID());
                } else {
                    resultBuilder.addVerifiedSignatureKeyId(signature.getKeyID());
                }
            }
        } catch (PGPException | SignatureException e) {
            throw new IOException(e.getMessage(), e);
        }

    }

    @Override
    public int read() throws IOException {
        final int data = super.read();
        final boolean endOfStream = data == -1;
        if (endOfStream) {
            validateOnePassSignatures();
        } else {
            updateOnePassSignatures((byte) data);
        }
        return data;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);

        final boolean endOfStream = read == -1;
        if (endOfStream) {
            validateOnePassSignatures();
        } else {
            updateOnePassSignatures(b, off, read);
        }
        return read;
    }

    @Override
    public long skip(long n) {
        throw new UnsupportedOperationException("skip() is not supported.");
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException("mark() not supported.");
    }

    @Override
    public synchronized void reset() {
        throw new UnsupportedOperationException("reset() is not supported.");
    }

    @Override
    public boolean markSupported() {
        return false;
    }
}