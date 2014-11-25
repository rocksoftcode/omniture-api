import org.springframework.web.client.RestTemplate

beans {
  xmlns([ctx:'http://www.springframework.org/schema/context', ])
  ctx.'component-scan'('base-package':'com.tgt.search.reporting')
  ctx.'property-placeholder'('location':'classpath:omniture.properties,classpath:defaults.properties',
    'ignore-unresolvable': 'true', 'ignore-resource-not-found': 'true')
  restTemplate(RestTemplate)
}