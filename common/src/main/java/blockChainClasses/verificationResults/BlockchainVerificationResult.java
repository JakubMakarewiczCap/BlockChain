package blockChainClasses.verificationResults;

import blockChainClasses.Block;

public record BlockchainVerificationResult(BlockchainVerificationResultEnum verificationResult, Block invalidBlock) {
}
