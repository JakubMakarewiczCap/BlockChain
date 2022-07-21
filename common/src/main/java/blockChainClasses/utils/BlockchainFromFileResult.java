package blockChainClasses.utils;

import blockChainClasses.Blockchain;

public record BlockchainFromFileResult(Blockchain blockchain,
                                       BlockchainVerificationResult blockchainVerificationResult) {
}
