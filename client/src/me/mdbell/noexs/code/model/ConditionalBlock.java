package me.mdbell.noexs.code.model;

import java.util.ArrayList;
import java.util.List;

public class ConditionalBlock extends Block {

    private Condition condition;
    private Block block;
    private Block elseBlock;

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public ConditionalBlock(Condition condition, Block block) {
        this(condition, block, null);
    }

    public ConditionalBlock(Condition condition, Block block, Block elseBlock) {
        super();
        this.condition = condition;
        this.block = block;
        this.elseBlock = elseBlock;
    }

    public void setElseBlock(Block elseBlock) {
        this.elseBlock = elseBlock;
    }

    @Override
    public List<IInstruction> getInstructions() {

        List<IInstruction> res = new ArrayList<>();
        res.add(condition);
        res.addAll(block.getInstructions());
        if (elseBlock != null) {
            res.add(new ElseCondition());
            res.addAll(elseBlock.getInstructions());
        }

        res.add(new EndCondition());

        return res;
    }

}
