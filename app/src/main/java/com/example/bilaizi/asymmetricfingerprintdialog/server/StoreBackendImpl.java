package com.example.bilaizi.asymmetricfingerprintdialog.server;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A fake backend implementation of {@link StoreBackend}.
 */
public class StoreBackendImpl implements StoreBackend {

    private final Map<String, PublicKey> mPublicKeys = new HashMap<>();
    private final Set<Transaction> mReceivedTransactions = new HashSet<>();

    @Override
    public boolean verify(Transaction transaction, byte[] transactionSignature) {
        try {
            if (mReceivedTransactions.contains(transaction)) {
                // It verifies the equality of the transaction including the client nonce
                // So attackers can't do replay attacks.
                return false;
            }
            mReceivedTransactions.add(transaction);
            PublicKey publicKey = mPublicKeys.get(transaction.getUserId());
            Signature verificationFunction = Signature.getInstance("SHA256withECDSA");
            verificationFunction.initVerify(publicKey);
            verificationFunction.update(transaction.toByteArray());
            if (verificationFunction.verify(transactionSignature)) {
                // Transaction is verified with the public key associated with the user
                // Do some post purchase processing in the server
                return true;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            // In a real world, better to send some error message to the user
        }
        return false;
    }

    @Override
    public boolean verify(Transaction transaction, String password) {
        // As this is just a sample, we always assume that the password is right.
        return true;
    }

    @Override
    public boolean enroll(String userId, String password, PublicKey publicKey) {
        if (publicKey != null) {
            mPublicKeys.put(userId, publicKey);
        }
        // We just ignore the provided password here, but in real life, it is registered to the
        // backend.
        return true;
    }
}
