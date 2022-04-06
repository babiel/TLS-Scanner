/**
 * TLS-Server-Scanner - A TLS configuration and analysis tool based on TLS-Attacker
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsscanner.serverscanner.report.rating;

import de.rub.nds.scanner.core.constants.TestResult;
import de.rub.nds.scanner.core.report.rating.PropertyResultRatingInfluencer;
import de.rub.nds.scanner.core.report.rating.RatingInfluencer;
import de.rub.nds.scanner.core.report.rating.RatingInfluencers;
import de.rub.nds.tlsscanner.core.constants.TlsAnalyzedProperty;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.xml.bind.JAXBException;

public class DefaultInfluencersTest {

    public void createDefaultRatingInfluencers() throws IOException, JAXBException {
        LinkedList<RatingInfluencer> influencers = new LinkedList<>();

        // versions
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SSL_2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -300, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SSL_3,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_0,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_1,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -200, 1500)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_DTLS_1_0,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_DTLS_1_2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_DTLS_1_3,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_PFS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -200, 1500)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_NULL_CIPHERS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_FORTEZZA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_EXPORT,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -800, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ANON,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_DES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_3DES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SEED,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_IDEA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_RC2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_RC4,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_AEAD,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_POST_QUANTUM,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ONLY_PFS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_AES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CAMELLIA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ARIA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CHACHA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_RSA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_STATIC_DH,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_STATIC_ECDH,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_STATIC_ECDH,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_GOST,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SRP,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_PSK_PLAIN,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_PSK_RSA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_PSK_DHE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_PSK_ECDHE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_NEWHOPE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ECMQV,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ECMQV,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_STREAM_CIPHERS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_EXTENDED_MASTER_SECRET,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ENCRYPT_THEN_MAC,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TOKENBINDING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_MONTGOMERY_CURVES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SESSION_TICKET_RESUMPTION,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SESSION_ID_RESUMPTION,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SESSION_TICKET_ROTATION_HINT,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SECURE_RENEGOTIATION_EXTENSION,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_SECURE_RENEGOTIATION_EXTENSION,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_INSECURE_RENEGOTIATION,
            new PropertyResultRatingInfluencer(TestResult.TRUE,
                TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_EXTENSION_V1, TestResult.TRUE),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_INSECURE_RENEGOTIATION,
            new PropertyResultRatingInfluencer(TestResult.TRUE,
                TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_EXTENSION_V2, TestResult.TRUE),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_SECURE_RENEGOTIATION_CIPHERSUITE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_INSECURE_RENEGOTIATION,
            new PropertyResultRatingInfluencer(TestResult.TRUE,
                TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_CIPHERSUITE_V1, TestResult.TRUE),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_INSECURE_RENEGOTIATION,
            new PropertyResultRatingInfluencer(TestResult.TRUE,
                TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_CIPHERSUITE_V2, TestResult.TRUE),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_FALLBACK_SCSV,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));

        influencers.add(new RatingInfluencer(
            TlsAnalyzedProperty.SUPPORTS_TLS_COMPRESSION, new PropertyResultRatingInfluencer(TestResult.TRUE,
                TlsAnalyzedProperty.VULNERABLE_TO_CRIME, TestResult.TRUE),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 100)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ONLY_PRIME_MODULI,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -200, 1500)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_COMMON_DH_PRIMES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ONLY_SAFEPRIME_MODULI,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_HTTPS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_HSTS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_HSTS_PRELOADING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_HPKP,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_HPKP_REPORTING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(
            TlsAnalyzedProperty.SUPPORTS_HTTP_COMPRESSION, new PropertyResultRatingInfluencer(TestResult.TRUE,
                TlsAnalyzedProperty.VULNERABLE_TO_BREACH, TestResult.TRUE),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.PREFERS_PFS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.ENFORCES_PFS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.ENFORCES_CS_ORDERING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.ENFORCES_NAMED_GROUP_ORDERING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.ENFORCES_SIGNATURE_HASH_ALGORITHM_ORDERING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_VERSION_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_CIPHER_SUITE_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_EXTENSION_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_CIPHER_SUITE_LENGTH_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_COMPRESSION_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_ALPN_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_CLIENT_HELLO_LENGTH_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_EMPTY_LAST_EXTENSION_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_SIG_HASH_ALGORITHM_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_BIG_CLIENT_HELLO_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_NAMED_GROUP_INTOLERANCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_SECOND_CIPHER_SUITE_BYTE_BUG,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.REFLECTS_OFFERED_CIPHER_SUITES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.IGNORES_OFFERED_CIPHER_SUITES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.IGNORES_OFFERED_NAMED_GROUPS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.IGNORES_OFFERED_SIG_HASH_ALGOS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_BLEICHENBACHER,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -800, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_GENERAL_DROWN,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -800, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_PADDING_ORACLE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_HEARTBLEED,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -1200, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_TICKETBLEED,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -800, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_INVALID_CURVE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -1200, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_INVALID_CURVE_EPHEMERAL,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_INVALID_CURVE_EPHEMERAL_EXPLOITABLE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -800, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_POODLE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_SWEET_32,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -300, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_EARLY_CCS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -300, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_CRIME,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_BREACH,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_LOGJAM,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_FREAK,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_EXTENSION_V1,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_EXTENSION_V2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_CIPHERSUITE_V1,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_RENEGOTIATION_ATTACK_CIPHERSUITE_V2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.MISSES_MAC_APPDATA_CHECKS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.MISSES_MAC_FINISHED_CHECKS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.MISSES_VERIFY_DATA_CHECKS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.MISSES_GCM_CHECKS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.REUSES_GCM_NONCES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_CERTIFICATE_ISSUES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_WEAK_RANDOMNESS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -500, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.REUSES_EC_PUBLICKEY,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.REUSES_DH_PUBLICKEY,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.REQUIRES_SNI,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_14,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_15,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_16,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_17,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_18,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_19,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_20,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_21,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_22,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_23,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_24,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_25,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_26,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_27,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS_1_3_DRAFT_28,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200, 1000),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_LEGACY_PRF,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SHA256_PRF,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SHA384_PRF,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ECDSA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_RSA_CERT,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_DSS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_EXPLICIT_PRIME_CURVE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_EXPLICIT_CHAR2_CURVE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS13_SESSION_TICKETS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_OCSP,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.MUST_STAPLE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.INCLUDES_CERTIFICATE_STATUS_MESSAGE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.STAPLED_RESPONSE_EXPIRED,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_NONCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.NONCE_MISMATCH,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.STAPLING_UNRELIABLE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_HTTP_FALSE_START,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_SESSION_TICKET_ZERO_KEY,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -1000, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_DIRECT_RACCOON,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -800, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_RACCOON_ATTACK,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_STAPLED_NONCE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 25),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_OCSP_STAPLING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.STAPLING_TLS13_MULTIPLE_CERTIFICATES,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.IGNORES_ECDSA_GROUP_DISPARITY,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_EXTRA_CLEAR_DROWN,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -800, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.VULNERABLE_TO_INVALID_CURVE_TWIST,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -1200, 500),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CCA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.REQUIRES_CCA,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS13_PSK_DHE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CERTIFICATE_STATUS_REQUEST,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CERTIFICATE_STATUS_REQUEST_V2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CERTIFICATE_STATUS_REQUEST_TLS13,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SCTS_PRECERTIFICATE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SCTS_HANDSHAKE,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_SCTS_OCSP,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_CHROME_CT_POLICY,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_UNCOMPRESSED_POINT,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ANSIX962_COMPRESSED_PRIME,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ANSIX962_COMPRESSED_CHAR2,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_TLS13_SECP_COMPRESSION,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -50),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.GROUPS_DEPEND_ON_CIPHER,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_ESNI,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));

        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.STRICT_ALPN,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.STRICT_SNI,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 100),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -50)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.ALPACA_MITIGATED,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -200)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_DTLS_FRAGMENTATION,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.SUPPORTS_REORDERING,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.PROCESSES_RETRANSMISSIONS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.HAS_COOKIE_CHECKS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, 0),
            new PropertyResultRatingInfluencer(TestResult.FALSE, -100)));
        influencers.add(new RatingInfluencer(TlsAnalyzedProperty.MISSES_MESSAGE_SEQUENCE_CHECKS,
            new PropertyResultRatingInfluencer(TestResult.TRUE, -200),
            new PropertyResultRatingInfluencer(TestResult.FALSE, 0)));
        RatingInfluencersIO.write(new File("src/main/resources/" + DefaultRatingLoader.INFLUENCERS_RESOURCE_LOCATION),
            new RatingInfluencers(influencers));
    }
}