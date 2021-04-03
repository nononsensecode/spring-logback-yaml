val getConfig: () -> String by extra
apply(from = getConfig())

val applicationProperties: HashMap<String, String> by extra

tasks.withType<ProcessResources> {
    filesMatching("application.yaml") {
        expand(applicationProperties)
    }
}