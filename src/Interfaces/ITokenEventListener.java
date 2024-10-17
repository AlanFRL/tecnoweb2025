
package Interfaces;

import Comunication.TokenEvent;


public interface ITokenEventListener {    
    void usuario(TokenEvent event);
    
    
    void error(TokenEvent event);
    void posibleserrores(TokenEvent event);

}
