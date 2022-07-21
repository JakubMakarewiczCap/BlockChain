package blockChainClasses.utils;

import blockChainClasses.Block;
import blockChainClasses.Blockchain;

public record BlockchainVerificationResult(BlockchainVerificationResultEnum verificationResult, Block invalidBlock) {
}
