package me.deecaad.core.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.deecaad.core.compatibility.CompatibilityAPI;
import org.bukkit.block.Block;

import java.util.function.Predicate;

public class BlockPredicateType extends CommandArgumentType<Predicate<Block>> {

    @Override
    public ArgumentType<?> getBrigadierType() {
        return CompatibilityAPI.getCommandCompatibility().blockPredicate();
    }

    @Override
    public Predicate<Block> parse(CommandContext<Object> context, String key) throws CommandSyntaxException {
        return CompatibilityAPI.getCommandCompatibility().getBlockPredicate(context, key);
    }
}
