package com.wilaya.utilisateur_service.domain.port.in;

import com.wilaya.utilisateur_service.domain.model.Agent;
import java.util.List;

public interface ListerAdminsUseCase {
    List<Agent> listerAdmins();
}