package in.jetbra.plugins.mymap;

import com.janetfilter.core.models.FilterRule;
import com.janetfilter.core.plugin.MyTransformer;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.List;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class MapTransformer implements MyTransformer {
    private final List<FilterRule> rules;

    public MapTransformer(List<FilterRule> rules) {
        this.rules = rules;
    }

    @Override
    public String getHookClassName() {
        return "com/google/gson/internal/LinkedTreeMap";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        PutFilter.setRules(rules);

        ClassReader reader = new ClassReader(classBytes);
        ClassNode node = new ClassNode(ASM5);
        reader.accept(node, 0);

        for (MethodNode mn : node.methods) {
            if ("put".equals(mn.name) && "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;".equals(mn.desc)) {
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC, "in/jetbra/plugins/mymap/PutFilter", "testPut", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
                list.add(new VarInsnNode(ASTORE, 2));

                mn.instructions.insert(list);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
