package com.etec.zl.conecta.Application.Mappers.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOContatos;
import com.etec.zl.conecta.Application.DTOs.Messages.DTORegisterMessage;
import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessage;
import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessageSecretaria;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.ValueObjects.Name;

public class MessageMapper {

    public DTOReturnMessage toReturn(Name name, Message message){
        return new DTOReturnMessage(
                name,
                message.getContent(),
                message.getMidia()
        );
    }
    public Message toRegister(DTORegisterMessage dto){
        return new Message(
                dto.idSender(),
                dto.idReceiver(),
                dto.dto().content(),
                dto.dto().midia()
        );
    }
    public DTOReturnMessageSecretaria toReturnSecretaria(Name nameSender, Name nameReceiver, Message message){
        return new DTOReturnMessageSecretaria(
                message.getId(),
                nameSender,
                message.getIdSender(),
                nameReceiver,
                message.getIdReceiver(),
                message.getTimestamp(),
                message.getContent(),
                message.getMidia()
                );
    }
    public DTOContatos toReturnContatos(Name name, String id){
        return new DTOContatos(name, id);
    }
}
