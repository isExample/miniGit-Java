package cli.converter;

import base.MiniGitCore;
import picocli.CommandLine;

public class OidConverter implements CommandLine.ITypeConverter<String> {
    @Override
    public String convert(String value) {
        return MiniGitCore.getOid(value);
    }
}
