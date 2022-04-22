package com.andrei1058.bedwars.proxy.party;

import com.andrei1058.bedwars.proxy.api.Messages;
import dev._2lstudios.advancedparties.api.PartyAPI;
import dev._2lstudios.advancedparties.parties.PartyDisbandReason;
import dev._2lstudios.advancedparties.players.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.andrei1058.bedwars.proxy.language.Language.getMsg;

public class AdvancedParties implements Party {
    private final dev._2lstudios.advancedparties.AdvancedParties ap = (dev._2lstudios.advancedparties.AdvancedParties) Bukkit.getServer().getPluginManager().getPlugin("AdvancedParties");

    private PartyPlayer getPartyPlayer(UUID uuid) {
        for (PartyPlayer pp : ap.getPlayerManager().getPlayers()) {
            if (pp.getBukkitPlayer().getUniqueId().equals(uuid)) return pp;
        }
        return null;
    }

    @Override
    public boolean hasParty(UUID p) {
        PartyPlayer pp = getPartyPlayer(p);
        return pp != null && pp.isInParty();
    }

    @Override
    public int partySize(UUID p) {
        PartyPlayer pp = getPartyPlayer(p);
        if (pp == null) return 0;
        if (pp.getParty().getID() == null) return 0;
        dev._2lstudios.advancedparties.parties.Party party = pp.getParty();
        if (party == null) return 0;
        return party.getMembers().size();
    }

    @Override
    public boolean isOwner(UUID p) {
        PartyPlayer pp = getPartyPlayer(p);
        if (pp == null) return false;
        if (pp.getParty().getID() == null) return false;
        dev._2lstudios.advancedparties.parties.Party party = pp.getParty();
        if (party == null) return false;
        if (party.getLeader() == null) return false;
        return party.getLeader().equals(p);
    }

    @Override
    public List<UUID> getMembers(UUID p) {
        ArrayList<UUID> players = new ArrayList<>();
        PartyPlayer pp = getPartyPlayer(p);
        if (pp == null) return players;
        if (pp.getParty().getID() == null) return players;
        dev._2lstudios.advancedparties.parties.Party party = pp.getParty();
        if (party == null) return players;
        players.addAll(party.getMembers().stream().map((s -> Bukkit.getPlayerExact(s).getUniqueId())).collect(Collectors.toList()));
        return players;
    }

    @Override
    public void createParty(Player owner, Player... members) {
    }

    @Override
    public void addMember(UUID owner, Player member) {
    }

    @Override
    public void removeFromParty(UUID member) {
        PartyPlayer pp = getPartyPlayer(member);
        if (pp == null) return;
        if (pp.getParty().getID() == null) return;
        dev._2lstudios.advancedparties.parties.Party party = pp.getParty();
        if (party == null) return;
        if (party.getLeader() != null && party.getLeader().equals(member)){
            disband(member);
        } else {
            party.removeMember(pp);
            Player target = Bukkit.getPlayer(member);
            if (target != null) {
                for (PartyPlayer mem : party.getPlayers()) {
                    Player p = Bukkit.getPlayer(mem.getBukkitPlayer().getUniqueId());
                    if (p == null) continue;
                    if (!p.isOnline()) continue;
                    p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_LEAVE_SUCCESS).replace("{player}", target.getName()));
                }
            }
        }
    }

    @Override
    public void disband(UUID owner) {
        PartyPlayer pp = getPartyPlayer(owner);
        if (pp == null) return;
        if (pp.getParty().getID() == null) return;
        dev._2lstudios.advancedparties.parties.Party party = pp.getParty();
        if (party == null) return;
        for (PartyPlayer mem : party.getPlayers()) {
            Player p = Bukkit.getPlayer(mem.getName());
            if (p == null) continue;
            if (!p.isOnline()) continue;
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_DISBAND_SUCCESS));
        }
        party.disband(PartyDisbandReason.OTHER);
    }

    @Override
    public boolean isMember(UUID owner, UUID check) {
        PartyPlayer pp = getPartyPlayer(owner);
        if (pp == null) return false;
        if (pp.getParty().getID() == null) return false;
        dev._2lstudios.advancedparties.parties.Party party = pp.getParty();
        if (party == null) return false;
        return party.getMembers().contains(check);
    }

    @Override
    public void removePlayer(UUID owner, UUID target) {
        PartyPlayer pp = getPartyPlayer(target);
        if (pp == null) return;
        if (pp.getParty().getID() == null) return;
        dev._2lstudios.advancedparties.parties.Party party = pp.getParty();
        if (party == null) return;
        party.removeMember(pp);
        for (PartyPlayer mem : party.getPlayers()) {
            Player p = Bukkit.getPlayer(mem.getName());
            if (p == null) continue;
            if (!p.isOnline()) continue;
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_REMOVE_SUCCESS));
        }
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public UUID getOwner(UUID player) {
        PartyPlayer pp = getPartyPlayer(player);
        if (pp == null) return null;
        if (pp.getParty().getID() == null) return null;

        if (pp.getParty() == null) return null;
        if (pp.getParty().getLeader() == null) return null;
        return Bukkit.getPlayerExact(pp.getParty().getLeader()).getUniqueId();
    }
}
