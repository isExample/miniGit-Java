package cli.converter;

import base.MiniGitCore;
import picocli.CommandLine;

public class OidConverter implements CommandLine.ITypeConverter<String> {
    @Override
    public String convert(String value) {
        try {
            return MiniGitCore.getOid(value);
        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }
}
